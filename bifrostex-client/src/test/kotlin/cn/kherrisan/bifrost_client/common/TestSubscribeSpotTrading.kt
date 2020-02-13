package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.BifrostexClient
import cn.kherrisan.bifrostex_client.core.common.*
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.SymbolMetaInfo
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestSubscribeSpotTrading {

    abstract val name: ExchangeName
    var logger = LogManager.getLogger()
    lateinit var service: ExchangeService
    lateinit var spotTradingService: SpotTradingService
    lateinit var symbolMetaInfo: SymbolMetaInfo
    open val config = ExchangeRuntimeConfig()
    val vertx = Vertx.vertx()

    @BeforeAll
    fun init() {
        BifrostexClient.init()
        service = ExchangeFactory.build(name)
        spotTradingService = service.spotTradingService
        runBlocking {
            service.spotTradingService.getBalance()
        }
        symbolMetaInfo = service.metaInfo.symbolMetaInfo[BTC_USDT]!!
    }
}