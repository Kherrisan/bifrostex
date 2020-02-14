package cn.kherrisan.bifrostex_engine.service

import cn.kherrisan.bifrostex_client.core.common.DefaultCoroutineScope
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.DefaultSubscription
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_engine.EntityNotFoundException
import cn.kherrisan.bifrostex_engine.ExchangeSpotBalance
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import cn.kherrisan.bifrostex_engine.core.Exchange
import cn.kherrisan.bifrostex_engine.repository.VertxSpotBalanceRepository
import cn.kherrisan.bifrostex_engine.repository.VertxSpotOrderRepository
import cn.kherrisan.bifrostex_engine.repository.VertxTransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.apache.commons.lang3.time.DateUtils
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*
import javax.annotation.PostConstruct

abstract class AbstractSpot(
        val exchange: Exchange
) : AbstractMarket(exchange.service.buildDataAdaptor()), Spot
        , CoroutineScope by DefaultCoroutineScope(exchange.service.vertx) {

    val logger = LogManager.getLogger()
    val spotMarketService: SpotMarketService
        get() = exchange.service.spotMarketService
    val spotTradingService: SpotTradingService
        get() = exchange.service.spotTradingService
    val name: ExchangeName
        get() = exchange.name


    @Autowired
    lateinit var spotOrderRepository: VertxSpotOrderRepository

    @Autowired
    lateinit var spotBalanceRepository: VertxSpotBalanceRepository

    @Autowired
    lateinit var transactionRepository: VertxTransactionRepository

    @PostConstruct
    fun initAbstractSpot() {

    }

    suspend fun CoroutineScope.createOrder(symbol: Symbol, creator: suspend () -> TransactionResult): ExchangeSpotOrder {
        //api 创建订单
        val tr = creator()
        if (!tr.succeed) {
            error(tr)
        }
        //查询订单信息
        var order = getOrder(tr.tid, symbol)
                ?: throw EntityNotFoundException(ExchangeSpotOrder::class.simpleName!!, tr.tid, symbol)
        //标识本订单是系统下达的
        order.bfr = true
        loop@ while (true) {
            //判断订单状态
            when (order.state) {
                OrderStateEnum.FILLED, OrderStateEnum.CANCELED, OrderStateEnum.FAILED -> {
                    //如果是终结态，直接返回
                    break@loop
                }
                else -> {
                    //非终结态继续判断
                }
            }
            delay(500)
            order = getOrder(tr.tid, symbol)
                    ?: throw EntityNotFoundException(ExchangeSpotOrder::class.simpleName!!, tr.tid, symbol)
        }
        return order
    }

    override suspend fun CoroutineScope.getOrder(exOid: String, symbol: Symbol): ExchangeSpotOrder? {
        var order = spotOrderRepository.getByOrderId(exOid)
        //如果 db 中没有这个 order，或者这个 order 没有终结，就从 api 取 order，并更新
        //如果 order 已处于终结态，则直接返回
        if (order == null || (order.state != OrderStateEnum.FILLED && order.state != OrderStateEnum.CANCELED)) {
            try {
                val exo = spotTradingService.getOrderDetail(exOid, symbol)
                val nOrder = ExchangeSpotOrder(ExchangeName.HUOBI, exo)
                //如果库中有该订单，则继承他的 bfr 标志
                if (order != null) {
                    nOrder.bfr = order.bfr
                }
                order = nOrder
            } catch (e: Exception) {
                logger.error(e)
                return null
            }
            spotOrderRepository.upsertByExoid(order)
        }
        return order
    }

    override suspend fun CoroutineScope.limitBuy(symbol: Symbol, amount: BigDecimal, price: BigDecimal): ExchangeSpotOrder {
        val order = createOrder(symbol) {
            spotTradingService.limitBuy(symbol, price, amount)
        }
        return order
    }

    override suspend fun CoroutineScope.limitSell(symbol: Symbol, amount: BigDecimal, price: BigDecimal): ExchangeSpotOrder {
        val order = createOrder(symbol) {
            spotTradingService.limitSell(symbol, price, amount)
        }
        return order
    }

    override suspend fun CoroutineScope.marketBuy(symbol: Symbol, volume: BigDecimal): ExchangeSpotOrder {
        return createOrder(symbol) {
            spotTradingService.marketBuy(symbol, volume = volume)
        }
    }

    override suspend fun CoroutineScope.marketSell(symbol: Symbol, amount: BigDecimal): ExchangeSpotOrder {
        return createOrder(symbol) {
            spotTradingService.marketSell(symbol, amount)
        }
    }

    override suspend fun CoroutineScope.getBalance(currency: Currency): ExchangeSpotBalance {
        //应为 spotTradingService.getBalance() 本来就是获得的所有币种的余额，因此顺带着把其他币种的余额也更新一下好了。
        spotTradingService.getBalance().entries
                .forEach {
                    spotBalanceRepository.insert(ExchangeSpotBalance(name, it.value))
                }
        return spotBalanceRepository.getByExchangeAndCurrency(name, currency)!!
    }

    override suspend fun CoroutineScope.getOpenOrders(symbol: Symbol, start: Date?, end: Date?): List<ExchangeSpotOrder> {
        return getOrders(symbol, start, end)
                .filter { it.state == OrderStateEnum.SUBMITTED || it.state == OrderStateEnum.PARTIAL_FILLED }
    }

    override suspend fun CoroutineScope.getOrders(symbol: Symbol, start: Date?, end: Date?, state: OrderStateEnum?): List<ExchangeSpotOrder> {
        //默认窗口边界为当前时间
        val notNullEnd = end ?: Date()
        //获得窗口为 1 一天的订单列表
        val notNullStart = start ?: DateUtils.addDays(notNullEnd, -1)
        spotTradingService.getOrders(symbol, notNullStart, notNullEnd)
                .forEach {
                    spotOrderRepository.upsertByExoid(ExchangeSpotOrder(name, it))
                }
        return spotOrderRepository.get(name, symbol, start, end, state)
    }

    override suspend fun CoroutineScope.subscribeDepth(symbol: Symbol): DefaultSubscription<Depth> {
        return spotMarketService.subscribeDepth(symbol)
    }

    override suspend fun CoroutineScope.subscribeTrade(symbol: Symbol): DefaultSubscription<Trade> {
        return spotMarketService.subscribeTrade(symbol)
    }

    override suspend fun CoroutineScope.subscribeKline(symbol: Symbol): DefaultSubscription<Kline> {
        return spotMarketService.subscribeKline(symbol)
    }

    override suspend fun CoroutineScope.subscribeTicker(symbol: Symbol): DefaultSubscription<Ticker> {
        return spotMarketService.subscribeTicker(symbol)
    }

    override suspend fun CoroutineScope.subscribeBalance(symbol: Symbol): Job {
        throw NotImplementedError()
    }

    override suspend fun CoroutineScope.subscribeOrder(symbol: Symbol): Job {
        throw NotImplementedError()
    }
}