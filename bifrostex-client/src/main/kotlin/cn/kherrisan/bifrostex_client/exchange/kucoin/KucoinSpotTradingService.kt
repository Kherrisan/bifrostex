package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotTradingService
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class KucoinSpotTradingService @Autowired constructor(
        staticConfiguration: KucoinStaticConfiguration,
        dataAdaptor: KucoinSerivceDataAdaptor
) : AbstractSpotTradingService(staticConfiguration, dataAdaptor, KucoinAuthenticateService(staticConfiguration.spotTradingHttpHost)) {
    override suspend fun getBalance(): Map<Currency, SpotBalance> {
        throw NotImplementedError()
    }

    override suspend fun limitBuy(symbol: Symbol, price: BigDecimal, amount: BigDecimal): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun limitSell(symbol: Symbol, price: BigDecimal, amount: BigDecimal): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun marketBuy(symbol: Symbol, amount: BigDecimal?, volume: BigDecimal?): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun marketSell(symbol: Symbol, amount: BigDecimal): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun getOrderDetail(oid: String, symbol: Symbol): SpotOrder {
        throw NotImplementedError()
    }

    override suspend fun getOpenOrders(symbol: Symbol, size: Int): List<SpotOrder> {
        throw NotImplementedError()
    }

    override suspend fun cancelOrder(oid: String, symbol: Symbol): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun getFee(symbol: Symbol): SpotTradingFee {
        throw NotImplementedError()
    }

    override suspend fun getOrders(symbol: Symbol, start: Date, end: Date, state: OrderStateEnum?, size: Int): List<SpotOrder> {
        throw NotImplementedError()
    }
}