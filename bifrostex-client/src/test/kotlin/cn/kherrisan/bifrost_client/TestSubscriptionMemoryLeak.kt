package cn.kherrisan.bifrost_client

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.BTC
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.USDT
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.testng.annotations.Test

//@Test
class TestSubscriptionMemoryLeak {

    @Test(enabled = false)
    fun testMemoryLeak() {
        val vertx = Vertx.vertx()
        val exchange = ExchangeFactory.build(ExchangeName.GATEIO, vertx)
        runBlocking {
            val sub = exchange.spotMarketService.subscribeTrade(Symbol(BTC, USDT))
            while (true) {
                println(sub.receive())
            }
        }
    }
}