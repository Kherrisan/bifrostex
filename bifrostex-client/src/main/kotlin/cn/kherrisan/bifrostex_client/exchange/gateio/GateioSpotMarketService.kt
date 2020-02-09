package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.common.iid
import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class GateioSpotMarketService @Autowired constructor(
        staticConfiguration: GateioStaticConfiguration,
        dataAdaptor: GateioServiceDataAdaptor
) : AbstractSpotMarketService(staticConfiguration, dataAdaptor) {

    @Autowired
    override lateinit var dispatcher: GateioWebsocketDispatcher

    override suspend fun getSymbols(): List<Symbol> {
        val resp = get(publicUrl("/api2/1/pairs"))
        return jsonArray(resp).map { symbol(it.asString) }
    }

    override suspend fun getSymbolMetaInfo(): List<SymbolMetaInfo> {
        val resp = get(publicUrl("/api2/1/marketinfo"))
        return jsonObject(resp)["pairs"].asJsonArray.map { it.asJsonObject }
                .map {
                    it.entrySet().map { e ->
                        SymbolMetaInfo(symbol(e.key),
                                e.value.asJsonObject["min_amount"].asBigDecimal,
                                e.value.asJsonObject["amount_decimal_places"].asInt,
                                e.value.asJsonObject["decimal_places"].asInt,
                                e.value.asJsonObject["min_amount_b"].asBigDecimal.precision())
                    }.get(0)
                }.sortedBy { it.symbol.base.name }
    }

    override suspend fun getCurrencies(): List<Currency> {
        val symbols = getSymbols()
        return symbols.flatMap { listOf(it.base, it.quote) }
                .distinct().sortedBy { it.name }
    }

    override suspend fun getTicker(symbol: Symbol): Ticker {
        val resp = get(publicUrl("https://data.gateio.life/api2/1/ticker/${string(symbol)}"))
        val obj = jsonObject(resp)
        return Ticker(
                symbol,
                obj["quoteVolume"].asBigDecimal,
                obj["baseVolume"].asBigDecimal,
                BigDecimal.ZERO,
                obj["last"].asBigDecimal,
                obj["high24hr"].asBigDecimal,
                obj["low24hr"].asBigDecimal,
                obj["highestBid"].asBigDecimal,
                obj["lowestAsk"].asBigDecimal
        )
    }

    override suspend fun getDepths(symbol: Symbol, size: Int): Depth {
        val resp = get(publicUrl("https://data.gateio.life/api2/1/orderBook/${string(symbol)}"))
        val obj = jsonObject(resp)
        val askMap = HashMap<BigDecimal, BigDecimal>()
        val bidMap = HashMap<BigDecimal, BigDecimal>()
        obj["asks"].asJsonArray.map { it.asJsonArray }
                .forEach { askMap[it[0].asBigDecimal] = it[1].asBigDecimal }
        obj["bids"].asJsonArray.map { it.asJsonArray }
                .forEach { bidMap[it[0].asBigDecimal] = it[1].asBigDecimal }
        return Depth(symbol, MyDate(), askMap, bidMap)
    }

    /**
     * 返回交易记录
     *
     * gateio固定返回最近80条交易记录，当size小于80时，会对响应的数据做截取，出于数据效率的考虑，size至少为80
     *
     * @param symbol Symbol
     * @param size Int 最大为80
     * @return List<Trade>
     */
    override suspend fun getTrades(symbol: Symbol, size: Int): List<Trade> {
        val resp = get(publicUrl("https://data.gateio.life/api2/1/tradeHistory/${string(symbol)}"))
        val obj = jsonObject(resp)
        return obj["data"].asJsonArray.map { it.asJsonObject }
                .map {
                    Trade(symbol,
                            it["tradeID"].asString,
                            date(it["timestamp"].asLong.toString()),
                            it["amount"].asBigDecimal,
                            it["rate"].asBigDecimal,
                            orderSide(it["type"].asString)
                    )
                    // 反正最大80，做两次reverse也不会有太大损失
                }.reversed().subList(0, size).reversed()
    }

    override suspend fun getKlines(symbol: Symbol, periodEnum: KlinePeriodEnum, size: Int, since: Date?): List<Kline> {
        val secUnit = periodEnum.toSeconds()
        val params = mutableMapOf("group_sec" to secUnit.toString(),
                "range_hour" to (size * secUnit / 3600).toString())
        val resp = get(publicUrl("https://data.gateio.life/api2/1/candlestick2/${string(symbol)}"), params)
        return jsonObject(resp)["data"].asJsonArray
                .map { it.asJsonArray }
                .map {
                    Kline(
                            symbol,
                            MyDate(it[0].asLong),
                            it[5].asBigDecimal,
                            it[2].asBigDecimal,
                            it[3].asBigDecimal,
                            it[4].asBigDecimal,
                            it[1].asBigDecimal
                    )
                }
    }

    /**
     *
     * @param channel String "ticker:[symbol,arg0,arg1...]"形式，方便传入参数
     * @param dispatcher WebsocketDispatcher
     * @param resolver Function2<JsonElement, Subscription<T>, T>
     * @return Subscription<T>
     */
    override fun <T : Any> newSubscription(channel: String, dispatcher: WebsocketDispatcher, resolver: suspend CoroutineScope.(JsonElement, Subscription<T>) -> Unit): Subscription<T> {
        val comm = channel.indexOf(":")
        // 这里的channel并不是真正的channel
        val method = channel.substring(0, comm)
        val params = channel.substring(comm + 1)
        val args = JsonParser.parseString(params).asJsonArray
        // 真正的channel——ticker:$symbol
        val ch = "$method:${args[0].asString}"
        val subscription = Subscription(ch, dispatcher, resolver)
        subscription.subPacket = {
            val id = iid().toInt()
            (dispatcher as GateioWebsocketDispatcher).idMap[id] = ch
            Gson().toJson(mapOf(
                    "id" to id,
                    "method" to "$method.subscribe",
                    "params" to args
            ))
        }
        subscription.unsubPacket = {
            val id = iid().toInt()
            (dispatcher as GateioWebsocketDispatcher).idMap[id] = ch
            Gson().toJson(mapOf(
                    "id" to id,
                    "method" to "$method.unsubscribe",
                    "params" to args
            ))
        }
        return subscription
    }

    /**
     * 订阅深度数据
     *
     * Notify market depth update information,使用clean字段来表示是全量数据还是增量数据
     * 每秒一次，很佛系
     *
     * @param symbol Symbol
     * @return Subscription<Trade>
     */
    override suspend fun subscribeDepth(symbol: Symbol): Subscription<Depth> {
        val ch = "depth:${Gson().toJson(listOf(string(symbol), 5, "0.01"))}"
        val dispatcher = GateioSingleChannelDispatcher(staticConfig as GateioStaticConfiguration, "depth:${string(symbol)}")
        return newSubscription<Depth>(ch, dispatcher) { obj, sub ->
            val params = obj.asJsonObject["params"].asJsonArray
            val clean = params[0].asBoolean
            if (clean) {
                //全量
                val depth = depth(symbol, params[1].asJsonObject)
                sub.data = depth
            } else {
                //增量
                val inc = depth(symbol, params[1].asJsonObject)
                if (sub.data != null) {
                    val baseDepth = sub.data as Depth
                    baseDepth.merge(inc)
                    sub.deliver(baseDepth)
                }
            }
        }.subscribe()
    }

    override suspend fun subscribeDepthSnapshot(symbol: Symbol): Subscription<Depth> {
        throw NotImplementedError()
    }

    override suspend fun subscribeTrade(symbol: Symbol): Subscription<Trade> {
        val args = "trades:${Gson().toJson(listOf(string(symbol)))}"
        return newSubscription<Trade>(args) { obj, sub ->
            val params = obj.asJsonObject["params"].asJsonArray[1].asJsonArray
            params.map { it.asJsonObject }
                    .forEach {
                        val trade = Trade(symbol,
                                it["id"].asInt.toString(),
                                MyDate((it["time"].asFloat * 1000).toLong()),
                                it["amount"].asBigDecimal,
                                it["price"].asBigDecimal,
                                orderSide(it["type"].asString)
                        )
                        sub.deliver(trade)
                    }
        }.subscribe()
    }

    override suspend fun subscribeTicker(symbol: Symbol): Subscription<Ticker> {
        val args = "ticker:${Gson().toJson(listOf(string(symbol)))}"
        return newSubscription<Ticker>(args) { obj, sub ->
            val it = obj.asJsonObject["params"].asJsonArray[1].asJsonObject
            sub.deliver(Ticker(symbol,
                    it["baseVolume"].asBigDecimal,
                    it["quoteVolume"].asBigDecimal,
                    it["open"].asBigDecimal,
                    it["close"].asBigDecimal,
                    it["high"].asBigDecimal,
                    it["low"].asBigDecimal,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO))
        }.subscribe()
    }

    override suspend fun subscribeKline(symbol: Symbol, period: KlinePeriodEnum): Subscription<Kline> {
        val args = "kline:${listOf(string(symbol), string(period).toInt())}"
        val dispatcher = GateioWebsocketDispatcher(staticConfig as GateioStaticConfiguration)
        return newSubscription<Kline>(args, dispatcher) { obj, sub ->
            obj.asJsonObject["params"].asJsonArray.map { it.asJsonArray }
                    .map {
                        Kline(symbol,
                                MyDate(it[0].asLong * 1000),
                                it[1].asBigDecimal,
                                it[2].asBigDecimal,
                                it[3].asBigDecimal,
                                it[4].asBigDecimal,
                                it[5].asBigDecimal)
                    }
                    .forEach { sub.deliver(it) }
        }.subscribe()
    }
}