package cn.kherrisan.bifrostex_engine.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_engine.ExchangeSpotBalance
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import cn.kherrisan.bifrostex_engine.repository.VertxSpotBalanceRepository
import cn.kherrisan.bifrostex_engine.repository.VertxSpotOrderRepository
import cn.kherrisan.bifrostex_engine.service.AbstractSpot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class HuobiSpot(@Autowired exchange: Huobi) : AbstractSpot(exchange) {

    @Autowired
    lateinit var spotOrderRepository: VertxSpotOrderRepository

    @Autowired
    lateinit var spotBalanceRepository: VertxSpotBalanceRepository

    override suspend fun getOrder(exOid: String, symbol: Symbol): ExchangeSpotOrder? {
        val q = Query()
        q.addCriteria(Criteria.where("exOid").`is`(exOid))
        var order = spotOrderRepository.getByOrderId(exOid)
        //如果 db 中没有这个 order，或者这个 order 没有终结，就从 api 取 order，并更新
        //如果 order 已处于终结态，则直接返回
        if (order == null || (order.state != OrderStateEnum.FILLED && order.state != OrderStateEnum.CANCELED)) {
            try {
                val o = spotTradingService.getOrderDetail(exOid, symbol)
                order = ExchangeSpotOrder(ExchangeName.HUOBI, o)
            } catch (e: Exception) {
                logger.error(e)
                return null
            }
            spotOrderRepository.upsert(order)
        }
        return order
    }

    override suspend fun CoroutineScope.limitBuy(symbol: Symbol, amount: BigDecimal, price: BigDecimal): TransactionResult {
        val tr: TransactionResult
        try {
            tr = spotTradingService.limitBuy(symbol, price, amount)
            if (!tr.succeed) {
                return tr
            }
        } catch (e: Exception) {
            logger.error(e)
            return TransactionResult("", false, e.localizedMessage)
        }
        var order = getOrder(tr.tid, symbol) ?: return TransactionResult(tr.tid, false)
        var res: TransactionResult
        loop@ while (true) {
            when (order.state) {
                OrderStateEnum.FILLED, OrderStateEnum.CANCELED -> {
                    res = TransactionResult(tr.tid)
                    break@loop
                }
                OrderStateEnum.FAILED -> {
                    res = TransactionResult(tr.tid, false)
                    break@loop
                }
                else -> {
                }
            }
            delay(500)
            order = getOrder(tr.tid, symbol) ?: return TransactionResult(tr.tid, false)
        }
        //order 终结，账目的金额发生变化了
        balanceDirtyMap[order.symbol.base] = true
        balanceDirtyMap[order.symbol.quote] = true
        //TODO：更新流水记录
        return res
    }

    override suspend fun limitSell(symbol: Symbol, amount: BigDecimal, price: BigDecimal): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun marketBuy(symbol: Symbol, amount: BigDecimal): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun marketSell(symbol: Symbol, amount: BigDecimal): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun getBalance(currency: Currency): ExchangeSpotBalance {
        if (isBalanceDirty(currency)) {
            spotTradingService.getBalance().entries
                    .filter { isBalanceDirty(it.key) }
                    .forEach {
                        spotBalanceRepository.insert(ExchangeSpotBalance(ExchangeName.HUOBI, it.value))
                        balanceDirtyMap[it.key] = false
                    }
        }
        return spotBalanceRepository.getByExchangeAndCurrency(ExchangeName.HUOBI,currency)!!
    }

    override suspend fun getOpenOrders(symbol: Symbol?, start: Date?, end: Date?): List<SpotOrder> {
        throw NotImplementedError()
    }

    override suspend fun getOrders(symbol: Symbol?, start: Date?, end: Date?, state: OrderStateEnum?): List<SpotOrder> {
        throw NotImplementedError()
    }
}