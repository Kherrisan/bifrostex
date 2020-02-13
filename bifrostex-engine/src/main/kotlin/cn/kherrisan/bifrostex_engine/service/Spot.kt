package cn.kherrisan.bifrostex_engine.service

import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.websocket.DefaultSubscription
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_engine.ExchangeSpotBalance
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
interface Spot {

    suspend fun CoroutineScope.subscribeBalance(symbol: Symbol): Job

    suspend fun CoroutineScope.subscribeOrder(symbol: Symbol): Job

    /**
     * 订阅深度数据
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @return Subscription<Depth>
     */
    suspend fun CoroutineScope.subscribeDepth(symbol: Symbol): DefaultSubscription<Depth>

    /**
     * 订阅公共交易数据
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @return Subscription<Trade>
     */
    suspend fun CoroutineScope.subscribeTrade(symbol: Symbol): DefaultSubscription<Trade>

    /**
     * 订阅 K线数据
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @return Subscription<Kline>
     */
    suspend fun CoroutineScope.subscribeKline(symbol: Symbol): DefaultSubscription<Kline>

    /**
     * 订阅聚合数据
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @return Subscription<Ticker>
     */
    suspend fun CoroutineScope.subscribeTicker(symbol: Symbol): DefaultSubscription<Ticker>

    /**
     *
     * @receiver CoroutineScope
     * @param exOid String
     * @param symbol Symbol
     * @return ExchangeSpotOrder?
     */
    suspend fun CoroutineScope.getOrder(exOid: String, symbol: Symbol): ExchangeSpotOrder?

    /**
     * 下限价买订单
     *
     * @receiver CoroutineScope
     * @param symbol Symbol 交易对
     * @param amount BigDecimal 数量
     * @param price BigDecimal 限价价格
     * @return ExchangeSpotOrder
     */
    suspend fun CoroutineScope.limitBuy(symbol: Symbol, amount: BigDecimal, price: BigDecimal): ExchangeSpotOrder

    /**
     * 下限价卖订单
     *
     * @receiver CoroutineScope
     * @param symbol Symbol 交易对
     * @param amount BigDecimal 数量
     * @param price BigDecimal 限价价格
     * @return ExchangeSpotOrder
     */
    suspend fun CoroutineScope.limitSell(symbol: Symbol, amount: BigDecimal, price: BigDecimal): ExchangeSpotOrder

    /**
     * 下市价买订单
     *
     * @receiver CoroutineScope
     * @param symbol Symbol 交易对
     * @param volume BigDecimal 金额（quote 数量）
     * @return ExchangeSpotOrder
     */
    suspend fun CoroutineScope.marketBuy(symbol: Symbol, volume: BigDecimal): ExchangeSpotOrder

    /**
     * 下市价卖订单
     *
     * @receiver CoroutineScope
     * @param symbol Symbol 交易对
     * @param amount BigDecimal 数量（base）
     * @return ExchangeSpotOrder
     */
    suspend fun CoroutineScope.marketSell(symbol: Symbol, amount: BigDecimal): ExchangeSpotOrder

    /**
     * 获得某币种余额
     *
     * @receiver CoroutineScope
     * @param currency Currency
     * @return ExchangeSpotBalance
     */
    suspend fun CoroutineScope.getBalance(currency: Currency): ExchangeSpotBalance

    /**
     * 获得未完结订单列表
     *
     * 未完结状态包含 SUBMITTED 和 PARTIALLY_FILLED 状态。
     * 如传入参数为 null，则表示在查询时不做限制。
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @param start Date?
     * @param end Date?
     * @return List<ExchangeSpotOrder>
     */
    suspend fun CoroutineScope.getOpenOrders(symbol: Symbol, start: Date? = null, end: Date? = null): List<ExchangeSpotOrder>

    /**
     * 获得订单列表
     *
     * 如传入参数为 null，则表示在查询时不做限制。
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @param start Date?
     * @param end Date?
     * @param state OrderStateEnum?
     * @return List<ExchangeSpotOrder>
     */
    suspend fun CoroutineScope.getOrders(symbol: Symbol, start: Date? = null, end: Date? = null, state: OrderStateEnum? = null): List<ExchangeSpotOrder>

}