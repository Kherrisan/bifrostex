package cn.kherrisan.bifrostex_engine.service

import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_engine.core.Exchange
import kotlinx.coroutines.newSingleThreadContext
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractSpot(
        val exchange: Exchange
) : Spot {

    val singleThread = newSingleThreadContext("${this::class.java.simpleName}Context")
    val balanceDirtyMap: MutableMap<Currency, Boolean> = ConcurrentHashMap()

    val logger = LogManager.getLogger()

    val spotMarketService: SpotMarketService
        get() = exchange.service.spotMarketService

    val spotTradingService: SpotTradingService
        get() = exchange.service.spotTradingService

    open fun isBalanceDirty(c: Currency): Boolean {
        if (!balanceDirtyMap.containsKey(c)) {
            balanceDirtyMap[c] = true
        }
        return balanceDirtyMap[c]!!
    }



}