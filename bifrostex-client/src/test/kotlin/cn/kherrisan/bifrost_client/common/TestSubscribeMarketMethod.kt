package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.*
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotMarketService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.SymbolMetaInfo
import org.apache.logging.log4j.LogManager
import org.junit.BeforeClass


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
        service = ExchangeFactory.build(name, config)
        spotMarketService = service.spotMarketService
        symbolMetaInfo = ((spotMarketService as AbstractSpotMarketService).dataAdaptor as AbstractServiceDataAdaptor).metaInfo.symbolMetaInfo[symbol]!!
    }
}