package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.entity.Symbol
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.testng.annotations.BeforeClass

/**
 * 各交易所市场行情HTTP接口的测试类
 *
 * 调用几个获取市场行情的HTTP接口以获得结果数据，并对结果进行一定的合理校验
 *
 * @property name ExchangeName 交易所名称
 * @property symbol Symbol 默认的交易货币
 * @property logger (org.apache.logging.log4j.Logger..org.apache.logging.log4j.Logger?)
 * @property service ExchangeService
 * @property vertx (io.vertx.core.Vertx..io.vertx.core.Vertx?)
 * @property spotMarketService MarketService
 */
abstract class TestQueryMarketMethod {

    abstract val name: ExchangeName
    open val symbol: Symbol = Symbol("btc", "usdt")
    open val rtConfig: RuntimeConfiguration = RuntimeConfiguration()
    var logger = LogManager.getLogger()

    lateinit var service: ExchangeService
    val vertx = Vertx.vertx()
    lateinit var spotMarketService: SpotMarketService

    @BeforeClass
    fun init() {
        service = ExchangeFactory.build(name, vertx, rtConfig)
        spotMarketService = service.spotMarketService
        runBlocking {
            service.awaitInitialization()
        }
    }
}