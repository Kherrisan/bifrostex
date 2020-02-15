package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.BifrostexClient
import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.SymbolMetaInfo
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestSubscribeMarketMethod {

    abstract val name: ExchangeName
    open val symbol: Symbol = Symbol("btc", "usdt")
    var logger = LogManager.getLogger()
    lateinit var service: ExchangeService
    lateinit var spotMarketService: SpotMarketService
    lateinit var symbolMetaInfo: SymbolMetaInfo
    open val config = ExchangeRuntimeConfig()
    val vertx = Vertx.vertx()

    @BeforeAll
    fun init() {
        BifrostexClient.init()
        service = ExchangeFactory.build(name)
        spotMarketService = service.spotMarketService
        runBlocking {
            service.spotMarketService.getSymbols()
        }
        symbolMetaInfo = service.metaInfo.symbolMetaInfo[symbol]!!
    }
}