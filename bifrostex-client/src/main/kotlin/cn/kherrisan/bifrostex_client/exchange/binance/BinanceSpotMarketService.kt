package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.common.iid
import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.ResolvableSubscription
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class BinanceSpotMarketService @Autowired constructor(
        staticConfiguration: BinanceStaticConfiguration,
        dataAdaptor: BinanceServiceDataAdaptor,
        metaInfo: BinanceMetaInfo,
        val runtimeConfig: BinanceRuntimeConfig
) : AbstractSpotMarketService(staticConfiguration, dataAdaptor, metaInfo) {

    @Autowired
    private lateinit var vertx: Vertx

    @Autowired
    override lateinit var dispatcher: BinanceWebsocketDispatcher

    override fun checkResponse(resp: HttpResponse<Buffer>): JsonElement {
        val obj = JsonParser.parseString(resp.bodyAsString())
        if (resp.statusCode() != 200) {
            logger.error(obj)
            error(obj)
        }
        return obj
    }

    override suspend fun getSymbols(): List<Symbol> {
        val resp = get(publicUrl("/api/v3/exchangeInfo"))
        val obj = jsonObject(resp)
        return obj["symbols"].asJsonArray.map { it.asJsonObject }
                .map {
                    Symbol(it["quoteAsset"].asString.toLowerCase(),
                            it["baseAsset"].asString.toLowerCase()
                    )
                }
                .sortedBy { it.base.name }
    }

    override suspend fun getSymbolMetaInfo(): List<SymbolMetaInfo> {
        //交易规范信息
        val resp = get(publicUrl("/api/v3/exchangeInfo"))
        val obj = jsonObject(resp)
        return obj["symbols"].asJsonArray.map { it.asJsonObject }
                .map {
                    SymbolMetaInfo(
                            Symbol(it["baseAsset"].asString.toLowerCase(),
                                    it["quoteAsset"].asString.toLowerCase()
                            ),
                            0f.toBigDecimal(),
                            it["baseAssetPrecision"].asInt,
                            it["quotePrecision"].asInt,
                            it["quotePrecision"].asInt
                    )
                }.sortedBy { it.symbol.base.name }
    }

    override suspend fun getCurrencies(): List<Currency> {
        return getSymbols().flatMap { listOf(it.base, it.quote) }
                .distinct()
                .sortedBy { it.name }
    }

    override suspend fun getTicker(symbol: Symbol): Ticker {
        val resp = get(publicUrl("/api/v3/ticker/24hr"), mutableMapOf("symbol" to string(symbol)))
        val it = jsonObject(resp)
        return Ticker(
                symbol,
                size(it["volume"], symbol),
                volume(it["quoteVolume"], symbol),
                price(it["openPrice"], symbol),
                price(it["lastPrice"], symbol),
                price(it["highPrice"], symbol),
                price(it["lowPrice"], symbol),
                price(it["bidPrice"], symbol),
                price(it["askPrice"], symbol)
        )
    }

    override suspend fun getDepths(symbol: Symbol, size: Int): Depth {
        logger.debug("Start to request depths: ${string(symbol)}")
        val resp = get(publicUrl("/api/v3/depth"), mutableMapOf(
                "symbol" to string(symbol),
                "limit" to size.toString()
        ))
        val obj = jsonObject(resp)
        return SequentialDepth(depth(symbol, obj), obj["lastUpdateId"].asLong, 0L)
    }

    override suspend fun getTrades(symbol: Symbol, size: Int): List<Trade> {
        val resp = get(publicUrl("/api/v3/trades"),
                mutableMapOf(
                        "symbol" to string(symbol),
                        "limit" to size.toString()
                ))
        val arr = jsonArray(resp)
        return arr.map { it.asJsonObject }
                .map {
                    Trade(symbol,
                            it["id"].asString,
                            date(it["time"].asLong.toString()),
                            size(it["qty"], symbol),
                            price(it["price"], symbol),
                            orderSide(it["isBuyerMaker"].asBoolean.toString())
                    )
                }
    }

    /**
     * 获得K线数据
     *
     * @param symbol Symbol
     * @param periodEnum KlinePeriodEnum
     * @param size Int 最大1000
     * @param since Date?
     * @return List<Kline>
     */
    override suspend fun getKlines(symbol: Symbol, periodEnum: KlinePeriodEnum, size: Int, since: Date?): List<Kline> {
        val params = mutableMapOf(
                "symbol" to symbol.nameWithoutSlash().toUpperCase(),
                "interval" to string(periodEnum),
                "limit" to size.toString())
        since?.let { params["startTime"] = string(since) }
        val resp = get(publicUrl("/api/v3/klines"), params)
        val arr = jsonArray(resp)
        return arr.map { it.asJsonArray }
                .map {
                    Kline(
                            symbol,
                            date(it[0].asLong.toString()),
                            price(it[1], symbol),
                            price(it[4], symbol),
                            price(it[2], symbol),
                            price(it[3], symbol),
                            volume(it[5], symbol)
                    )
                }
                .sortedBy { it.time }
    }

    override fun <T : Any> newSubscription(channel: String, dispatcher: WebsocketDispatcher, resolver: suspend CoroutineScope.(JsonElement, ResolvableSubscription<T>) -> Unit): ResolvableSubscription<T> {
        val subscription = ResolvableSubscription(channel, dispatcher, resolver)
        subscription.subPacket = {
            val id = iid().toInt()
            (dispatcher as BinanceWebsocketDispatcher).idMap[id] = channel
            Gson().toJson(mapOf(
                    "method" to "SUBSCRIBE",
                    "params" to listOf(channel),
                    "id" to id
            ))
        }
        subscription.unsubPacket = {
            val id = iid().toInt()
            (dispatcher as BinanceWebsocketDispatcher).idMap[id] = channel
            Gson().toJson(mapOf(
                    "method" to "UNSUBSCRIBE",
                    "params" to listOf(channel),
                    "id" to id
            ))
        }
        return subscription
    }

    /**
     * 订阅深度信息
     *
     * 1000档数据，增量更新，每100ms更新一次。
     *
     * @param symbol Symbol
     * @return Subscription<Depth>
     */
    override suspend fun subscribeDepth(symbol: Symbol): Subscription<Depth> {
        var baseDepthPromise = Promise.promise<SequentialDepth>()
        val ch = "${symbol.nameWithoutSlash()}@depth@100ms"
        val dedicatedDispatcher = BinanceSingleChannelDispatcher(staticConfig as BinanceStaticConfiguration, ch, runtimeConfig)
        val sub = newSubscription<Depth>(ch, dedicatedDispatcher) { it, sub ->
            try {
                val obj = it.asJsonObject
                val askMap = ConcurrentHashMap<BigDecimal, BigDecimal>()
                val bidMap = ConcurrentHashMap<BigDecimal, BigDecimal>()
                obj["a"].asJsonArray.map { it.asJsonArray }.forEach {
                    askMap[price(it[0], symbol)] = size(it[1], symbol)
                }
                obj["b"].asJsonArray.map { it.asJsonArray }.forEach {
                    bidMap[price(it[0], symbol)] = size(it[1], symbol)
                }
                //Binance的增量数据代表了一个事件区间内的ask和bid的变化量，并且给出了该事件区间开始的序号和结束的序号
                //各个增量的序号不会重叠，相连的增量的序号差为1
                //这里使用开始的序号作为prev，结束的序号作为seq
                val inc = SequentialDepth(symbol, MyDate(), askMap, bidMap, obj["u"].asLong, obj["U"].asLong)
                sub.buffer.add(inc)
                if (baseDepthPromise.future().isComplete && sub.data == null) {
                    sub.data = baseDepthPromise.future().result()
                }
                if (sub.data != null) {
                    //更新增量数据
                    val baseDepth = sub.data as SequentialDepth
                    val oldDepth = sub.buffer.map { it as SequentialDepth }.filter { it.seq <= baseDepth.seq }
                    //把老旧的增量数据删除
                    sub.buffer.removeAll(oldDepth)
                    for (i in sub.buffer.map { it as SequentialDepth }.sortedBy { it.prev }) {
                        if (i.prev > baseDepth.seq + 1) {
                            break
                        } else if (baseDepth.prev == 0L) {
                            //落在第一个增量数据的区间内[prev,seq]，且全量数据没有更新过
                            baseDepth.merge(i)
                            baseDepth.prev = i.prev
                            baseDepth.seq = i.seq
                        } else if (baseDepth.seq + 1 == i.prev) {
                            baseDepth.merge(i)
                            baseDepth.seq = i.seq
                        } else {
                            //增量数据不连续
                            launch(vertx.dispatcher()) {
                                baseDepthPromise = Promise.promise()
                                val newBaseDepth = getDepths(symbol, 1000) as SequentialDepth
                                baseDepthPromise.complete(newBaseDepth)
                            }
                        }
                    }
                    sub.deliver(baseDepth)
                }
            } catch (e: Exception) {
                logger.error(e)
                logger.error(it)
            }
        }.subscribe()
        delay(1000)
        val baseDepth = getDepths(symbol, 1000) as SequentialDepth
        baseDepthPromise.complete(baseDepth)
        return sub
    }

    override suspend fun subscribeDepthSnapshot(symbol: Symbol): Subscription<Depth> {
        val ch = "${symbol.nameWithoutSlash()}@depth5"
        val dispatcher = BinanceSingleChannelDispatcher(staticConfig as BinanceStaticConfiguration, ch, runtimeConfig)
        return newSubscription<Depth>(ch, dispatcher) { resp, sub ->
            sub.deliver(depth(symbol, resp.asJsonObject))
        }.subscribe()
    }

    override suspend fun subscribeTrade(symbol: Symbol): Subscription<Trade> {
        val ch = "${symbol.nameWithoutSlash()}@trade"
        return newSubscription<Trade>(ch) { it, sub ->
            val obj = it.asJsonObject
            sub.deliver(Trade(symbol,
                    obj["t"].asString,
                    date(obj["T"].asLong.toString()),
                    size(obj["q"], symbol),
                    price(obj["p"], symbol),
                    orderSide(obj["m"].asBoolean.toString())
            ))
        }.subscribe()
    }

    override suspend fun subscribeTicker(symbol: Symbol): Subscription<Ticker> {
        val ch = "${symbol.nameWithoutSlash()}@ticker"
        return newSubscription<Ticker>(ch) { it, sub ->
            val obj = it.asJsonObject
            sub.deliver(Ticker(symbol,
                    size(obj["q"], symbol),
                    volume(obj["v"], symbol),
                    price(obj["o"], symbol),
                    price(obj["c"], symbol),
                    price(obj["h"], symbol),
                    price(obj["l"], symbol),
                    price(obj["b"], symbol),
                    price(obj["a"], symbol)
            ))
        }.subscribe()
    }

    override suspend fun subscribeKline(symbol: Symbol, period: KlinePeriodEnum): Subscription<Kline> {
        val ch = "${symbol.nameWithoutSlash()}@kline_${string(period)}"
        return newSubscription<Kline>(ch) { it, sub ->
            val k = it.asJsonObject["k"].asJsonObject
            sub.deliver(Kline(symbol,
                    date(it.asJsonObject["E"].asLong.toString()),
                    price(k["o"], symbol),
                    price(k["c"], symbol),
                    price(k["h"], symbol),
                    price(k["l"], symbol),
                    size(k["v"], symbol)))
        }.subscribe()
    }
}