package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.SymbolMetaInfo
import org.apache.logging.log4j.LogManager
import org.testng.annotations.BeforeClass

abstract class TestSubscribeMarketMethod {

    abstract val name: ExchangeName
    open val symbol: Symbol = Symbol("btc", "usdt")
    var logger = LogManager.getLogger()
    lateinit var service: ExchangeService
    lateinit var spotMarketService: SpotMarketService
    lateinit var symbolMetaInfo: SymbolMetaInfo
    val vertx = io.vertx.core.Vertx.vertx()
    open val config = RuntimeConfiguration()

    @BeforeClass
    fun init() {
        service = ExchangeFactory.build(name, vertx, config)
        spotMarketService = service.spotMarketService
        symbolMetaInfo = service.metaInfo.symbolMetaInfo[symbol]!!
    }
}