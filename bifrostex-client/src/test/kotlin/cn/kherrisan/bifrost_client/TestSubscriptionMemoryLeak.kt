package cn.kherrisan.bifrost_client

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.BTC
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.USDT
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

//@Test
class TestSubscriptionMemoryLeak {

    @Test
    @Disabled
    fun testMemoryLeak() {
        val exchange = ExchangeFactory.build(ExchangeName.GATEIO)
        runBlocking {
            val sub = exchange.spotMarketService.subscribeTrade(Symbol(BTC, USDT))
            while (true) {
                println(sub.receive())
            }
        }
    }
}