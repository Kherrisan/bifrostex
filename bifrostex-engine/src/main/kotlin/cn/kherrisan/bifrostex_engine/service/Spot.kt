package cn.kherrisan.bifrostex_engine.service

import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_engine.ExchangeSpotBalance
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import kotlinx.coroutines.CoroutineScope
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
interface Spot {

    suspend fun getOrder(exOid: String, symbol: Symbol): ExchangeSpotOrder?

    suspend fun CoroutineScope.limitBuy(symbol: Symbol, amount: BigDecimal, price: BigDecimal): TransactionResult

    suspend fun limitSell(symbol: Symbol, amount: BigDecimal, price: BigDecimal): TransactionResult

    suspend fun marketBuy(symbol: Symbol, amount: BigDecimal): TransactionResult

    suspend fun marketSell(symbol: Symbol, amount: BigDecimal): TransactionResult

    suspend fun getBalance(currency: Currency): ExchangeSpotBalance

    suspend fun getOpenOrders(symbol: Symbol?, start: Date?, end: Date?): List<SpotOrder>

    suspend fun getOrders(symbol: Symbol?, start: Date?, end: Date?, state: OrderStateEnum?): List<SpotOrder>

}