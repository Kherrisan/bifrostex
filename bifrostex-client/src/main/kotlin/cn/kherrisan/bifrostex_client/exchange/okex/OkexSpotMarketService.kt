package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.DefaultSubscription
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class OkexSpotMarketService @Autowired constructor(
        staticConfiguration: OkexStaticConfiguration,
        dataAdaptor: OkexServiceDataAdaptor,
        metaInfo: OkexMetaInfo
) : AbstractSpotMarketService(staticConfiguration, dataAdaptor, metaInfo) {

    @Autowired
    override lateinit var dispatcher: OkexWebsocketDispatcher

    override fun checkResponse(resp: HttpResponse<Buffer>): JsonElement {
        val obj = JsonParser.parseString(resp.bodyAsString())
        if (resp.statusCode() != 200) {
            logger.error(obj)
            error(obj)
        }
        return obj
    }

    override suspend fun getSymbols(): List<Symbol> {
        val resp = get(publicUrl("/api/spot/v3/instruments"))
        return jsonArray(resp).map { it.asJsonObject }
                .map {
                    Symbol(it["base_currency"].asString.toLowerCase(),
                            it["quote_currency"].asString.toLowerCase())
                }.sortedBy { it.base.name }
    }

    override suspend fun getSymbolMetaInfo(): List<SymbolMetaInfo> {
        val resp = get(publicUrl("/api/spot/v3/instruments"))
        return jsonArray(resp).map { it.asJsonObject }
                .map {
                    SymbolMetaInfo(
                            Symbol(it["base_currency"].asString.toLowerCase(),
                                    it["quote_currency"].asString.toLowerCase()),
                            it["min_size"].asString.toBigDecimal(),
                            it["size_increment"].asString.toBigDecimal().scale(),
                            it["tick_size"].asString.toBigDecimal().scale(),
                            it["tick_size"].asString.toBigDecimal().scale())
                }.sortedBy { it.symbol.base.name }
    }

    /**
     * 获得所有currency
     *
     * @return List<Currency>
     */
    override suspend fun getCurrencies(): List<Currency> {
        val symbols = getSymbols()
        val set = mutableSetOf<Currency>()
        symbols.forEach {
            set.add(it.base)
            set.add(it.quote)
        }
        return set.toList().sortedBy { it.name }
    }

    override suspend fun getTicker(symbol: Symbol): Ticker {
        val resp = get(publicUrl("/api/spot/v3/instruments/${string(symbol)}/ticker"))
        val obj = jsonObject(resp)
        return Ticker(
                symbol,
                size(obj["base_volume_24h"], symbol),
                volume(obj["quote_volume_24h"], symbol),
                price(obj["open_24h"], symbol),
                price(obj["last"], symbol),
                price(obj["high_24h"], symbol),
                price(obj["low_24h"], symbol),
                price(obj["best_bid"], symbol),
                price(obj["best_ask"], symbol),
                date(obj["timestamp"].asString)
        )
    }

    /**
     * 返回深度信息
     *
     * 支持按价格合并深度，如0.1或0.001
     *
     * @param symbol Symbol
     * @param size Int 最大为200
     * @return Depth
     */
    override suspend fun getDepths(symbol: Symbol, size: Int): Depth {
        val params = mutableMapOf("size" to size.toString())
        val resp = get(publicUrl("/api/spot/v3/instruments/${string(symbol)}/book"), params)
        val obj = jsonObject(resp)
        return depth(symbol, obj)
    }

    /**
     * 获得成交数据
     *
     * @param symbol Symbol
     * @param size Int 最大为60
     * @return List<Trade>
     */
    override suspend fun getTrades(symbol: Symbol, size: Int): List<Trade> {
        val resp = get(publicUrl("/api/spot/v3/instruments/${string(symbol)}/trades"),
                mutableMapOf("limit" to size.toString()))
        val arr = jsonArray(resp)
        return arr.map { it.asJsonObject }
                .map {
                    Trade(symbol,
                            it["trade_id"].asString,
                            date(it["timestamp"].asString),
                            size(it["size"], symbol),
                            price(it["price"], symbol),
                            orderSide(it["side"].asString)
                    )
                }
                .sortedBy { it.time }
    }

    /**
     * 返回K线数据
     *
     * @param symbol Symbol
     * @param periodEnum KlinePeriodEnum
     * @param size Int
     * @param since Date? since不填则按时间粒度返回最近的200条数据
     * @return List<Kline>
     */
    override suspend fun getKlines(symbol: Symbol, periodEnum: KlinePeriodEnum, size: Int, since: Date?): List<Kline> {
        val params = mutableMapOf("granularity" to string(periodEnum))
        if (since != null) {
            params["start"] = string(since)
            val end = Date(since.time + periodEnum.toSeconds() * 1000 * size)
            params["end"] = string(end)
        }
        val resp = get(publicUrl("/api/spot/v3/instruments/${string(symbol)}/candles"), params)
        val arr = jsonArray(resp)
        return arr.map { it.asJsonArray }
                .map {
                    Kline(symbol,
                            date(it[0].asString),
                            price(it[1], symbol),
                            price(it[4], symbol),
                            price(it[2], symbol),
                            price(it[3], symbol),
                            volume(it[5], symbol)
                    )
                }
                .sortedBy { it.time }
    }

    override fun <T : Any> newSubscription(channel: String, dispatcher: WebsocketDispatcher, resolver: suspend CoroutineScope.(JsonElement, DefaultSubscription<T>) -> Unit): DefaultSubscription<T> {
        val subscription = DefaultSubscription<T>(channel, dispatcher, resolver)
        subscription.subPacket = { Gson().toJson(mapOf("op" to "subscribe", "args" to listOf(channel))) }
        subscription.unsubPacket = { Gson().toJson(mapOf("op" to "unsubscribe", "args" to listOf(channel))) }
        return subscription
    }

    /**
     * 订阅深度数据
     *
     * 订阅后首次返回市场订单簿的400档深度数据并推送；然后每隔100毫秒，快照这个时间段内有更改的订单簿数据，并推送。
     *
     * @param symbol Symbol
     * @return Subscription<Depth>
     */
    override suspend fun subscribeDepth(symbol: Symbol): Subscription<Depth> {
        val ch = "spot/depth:${string(symbol)}"
        return newSubscription<Depth>(ch) { it, sub ->
            //其中spot/depth 频道为了区分是首次全量和后续的增量返回格式将会是
            //{"table":"channel", "action":"<value>","data":"[{"<value1>","<value2>"}]"}
            //okex不使用seq
            val obj = it.asJsonObject
            if (obj["action"].asString == "partial") {
                //全量
                val data = obj["data"].asJsonArray.map { it.asJsonObject }
                        .find { it["instrument_id"].asString == string(symbol) }!!.asJsonObject
                val depth = depth(symbol, data)
                depth.time = date(data["timestamp"])
                sub.data = depth
            } else {
                //增量
                val data = obj["data"].asJsonArray.map { it.asJsonObject }
                        .find { it["instrument_id"].asString == string(symbol) }!!.asJsonObject
                val inc = depth(symbol, data)
                sub.buffer.add(inc)
                if (sub.data != null) {
                    val baseDepth = sub.data as Depth
                    val oldDepths = sub.buffer.map { it as Depth }.filter { it.time < baseDepth.time }
                    sub.buffer.removeAll(oldDepths)
                    for (increment in sub.buffer.map { it as Depth }.sortedBy { it.time }) {
                        baseDepth.merge(increment)
                        baseDepth.time = increment.time
                    }
                    sub.deliver(baseDepth)
                }
            }
        }.subscribe()
    }

    /**
     * 订阅深度数据
     *
     * 每隔100毫秒，快照当前时刻市场订单簿的5档深度数据并推送。
     *
     * @param symbol Symbol
     * @return Subscription<Depth>
     */
    override suspend fun subscribeDepthSnapshot(symbol: Symbol): Subscription<Depth> {
        val ch = "spot/depth5:${string(symbol)}"
        return newSubscription<Depth>(ch) { resp, sub ->
            val data = resp.asJsonObject["data"].asJsonArray[0].asJsonObject
            sub.deliver(depth(symbol, data))
        }.subscribe()
    }

    override suspend fun subscribeTrade(symbol: Symbol): Subscription<Trade> {
        val ch = "spot/trade:${string(symbol)}"
        return newSubscription<Trade>(ch) { resp, sub ->
            resp.asJsonObject["data"].asJsonArray.map { it.asJsonObject }
                    .map {
                        Trade(symbol,
                                it["trade_id"].asString,
                                date(it["timestamp"].asString),
                                size(it["size"], symbol),
                                price(it["price"], symbol),
                                if (it["side"].asString == "buy")
                                    OrderSideEnum.BUY
                                else
                                    OrderSideEnum.SELL
                        )
                    }.forEach { sub.deliver(it) }
        }.subscribe()
    }

    override suspend fun subscribeTicker(symbol: Symbol): Subscription<Ticker> {
        val ch = "spot/ticker:${string(symbol)}"
        return newSubscription<Ticker>(ch) { resp, sub ->
            val t = resp.asJsonObject["data"].asJsonArray[0].asJsonObject
            sub.deliver(Ticker(symbol,
                    size(t["base_volume_24h"], symbol),
                    volume(t["quote_volume_24h"], symbol),
                    price(t["open_24h"], symbol),
                    price(t["last"], symbol),
                    price(t["high_24h"], symbol),
                    price(t["low_24h"], symbol),
                    price(t["best_bid"], symbol),
                    price(t["best_ask"], symbol)))
        }.subscribe()
    }

    override suspend fun subscribeKline(symbol: Symbol, period: KlinePeriodEnum): Subscription<Kline> {
        val ch = "spot/candle${string(period)}s:${string(symbol)}"
        return newSubscription<Kline>(ch) { resp, sub ->
            val t = resp.asJsonObject["data"].asJsonArray[0].asJsonObject["candle"].asJsonArray
            sub.deliver(Kline(symbol,
                    date(t[0].asString),
                    price(t[1], symbol),
                    price(t[4], symbol),
                    price(t[2], symbol),
                    price(t[3], symbol),
                    size(t[5], symbol)))
        }.subscribe()
    }
}