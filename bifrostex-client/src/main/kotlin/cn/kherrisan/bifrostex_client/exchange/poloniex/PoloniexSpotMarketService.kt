package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

/**
 * Poloniex现货行情接口
 *
 * 和其他大部分交易所接口不同，Poloniex的接口不是遵循restful风格的，而是通过http请求中的command参数来标识查询的内容。
 *
 * @constructor
 */
@Component
class PoloniexSpotMarketService @Autowired constructor(
        staticConfiguration: PoloniexStaticConfiguration,
        dataAdaptor: PoloniexServiceDataAdaptor,
        metaInfo: PoloniexMetaInfo
) : AbstractSpotMarketService(staticConfiguration, dataAdaptor, metaInfo) {

    /**
     * 获得所有symbols
     *
     * 和getCurrencies()用的是同一个接口
     *
     * @return List<Symbol>
     */
    override suspend fun getSymbols(): List<Symbol> {
        val resp = get(publicUrl(""), mutableMapOf("command" to "returnTicker"))
        return jsonObject(resp).entrySet()
                .map { symbol(it.key) }
    }

    override suspend fun getSymbolMetaInfo(): List<SymbolMetaInfo> {
        throw NotImplementedError()
    }

    /**
     * 获得所有currencys
     *
     * { id: 1,
    name: '1CRedit',
    txFee: '0.01000000',
    minConf: 10000,
    depositAddress: null,
    disabled: 1,
    delisted: 1,
    frozen: 0 }
     * 本接口Poloniex还会顺带返回交易费率等其他信息。
     *
     * @return List<Currency>
     */
    override suspend fun getCurrencies(): List<Currency> {
        val resp = get(publicUrl(""), mutableMapOf("command" to "returnCurrencies"))
        return jsonObject(resp).entrySet()
                .map { PoloniexCurrency(it.key.toLowerCase(), it.value.asJsonObject["id"].asInt) }
                .sortedBy { it.name }
    }

    /**
     * 获得某个交易对的ticker数据
     *
     * Poloniex会直接返回所有symbol的ticker，然后本程序从响应中自行选择出对应的symbol。本接口的数据量较大，需要谨慎使用。其实可以单独搞一个获得所有货币ticker的方法。
     *
     * @param symbol Symbol
     * @return PoloniexTicker 其中open为0
     */
    override suspend fun getTicker(symbol: Symbol): Ticker {
        val resp = get(publicUrl(""), mutableMapOf("command" to "returnTicker"))
        val obj = jsonObject(resp)[string(symbol)].asJsonObject
        return PoloniexTicker(
                symbol,
                obj["baseVolume"].asBigDecimal,
                obj["quoteVolume"].asBigDecimal,
                // 没有open数据
                0f.toBigDecimal(),
                obj["last"].asBigDecimal,
                obj["high24hr"].asBigDecimal,
                obj["low24hr"].asBigDecimal,
                obj["highestBid"].asBigDecimal,
                obj["lowestAsk"].asBigDecimal,
                obj["id"].asInt
        )
    }

    override suspend fun getDepths(symbol: Symbol, size: Int): Depth {
        val resp = get(publicUrl(""), mutableMapOf(
                "command" to "returnOrderBook",
                "currencyPair" to string(symbol),
                "depth" to size.toString()
        ))
        val obj = jsonObject(resp)
        val askMap = HashMap<BigDecimal, BigDecimal>()
        val bidMap = HashMap<BigDecimal, BigDecimal>()
        obj["asks"].asJsonArray.map { it.asJsonArray }
                .forEach { askMap[it[0].asString.toBigDecimal()] = it[1].asBigDecimal }
        obj["bids"].asJsonArray.map { it.asJsonArray }
                .forEach { bidMap[it[0].asString.toBigDecimal()] = it[1].asBigDecimal }
        return Depth(symbol, MyDate(), askMap, bidMap)
    }

    override suspend fun getTrades(symbol: Symbol, size: Int): List<Trade> {
        val resp = get(publicUrl(""), mutableMapOf(
                "command" to "returnTradeHistory",
                "currencyPair" to string(symbol)
        ))
        return jsonArray(resp).map { it.asJsonObject }
                .map {
                    Trade(symbol,
                            it["tradeID"].asString,
                            date(it["date"].asString),
                            it["amount"].asBigDecimal,
                            it["rate"].asBigDecimal,
                            orderSide(it["type"].asString)
                    )
                }
    }

    override suspend fun getKlines(symbol: Symbol, periodEnum: KlinePeriodEnum, size: Int, since: Date?): List<Kline> {
        val start: Long
        val end: Long
        if (since == null) {
            end = System.currentTimeMillis() / 1000
            start = end - periodEnum.toSeconds() * size
        } else {
            start = since.time / 1000
            end = since.time + periodEnum.toSeconds() * size
        }
        val resp = get(publicUrl(""), mutableMapOf(
                "command" to "returnChartData",
                "currencyPair" to string(symbol),
                "period" to string(periodEnum),
                "start" to start.toString(),
                "end" to end.toString()
        ))
        return jsonArray(resp).map { it.asJsonObject }
                .map {
                    Kline(symbol,
                            MyDate(it["date"].asLong * 1000),
                            it["open"].asBigDecimal,
                            it["close"].asBigDecimal,
                            it["high"].asBigDecimal,
                            it["low"].asBigDecimal,
                            it["volume"].asBigDecimal
                    )
                }
    }

    override suspend fun subscribeDepth(symbol: Symbol): Subscription<Depth> {
        throw NotImplementedError()
    }

    override suspend fun subscribeDepthSnapshot(symbol: Symbol): Subscription<Depth> {
        throw NotImplementedError()
    }

    override suspend fun subscribeTrade(symbol: Symbol): Subscription<Trade> {
        throw NotImplementedError()
    }

    override suspend fun subscribeTicker(symbol: Symbol): Subscription<Ticker> {
        throw NotImplementedError()
    }

    override suspend fun subscribeKline(symbol: Symbol, period: KlinePeriodEnum): Subscription<Kline> {
        throw NotImplementedError()
    }
}