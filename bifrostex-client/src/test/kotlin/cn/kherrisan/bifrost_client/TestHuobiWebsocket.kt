package cn.kherrisan.bifrost_client

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.Symbol
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TestWebsocket {

//    @Test
    fun testSubscribeKline() {
        val vertx = Vertx.vertx()
        val service = ExchangeFactory.build(ExchangeName.BINANCE)
    val marketService = service.spotMarketService
        GlobalScope.launch(vertx.dispatcher()) {
            val subscription = marketService.subscribeDepth(Symbol("btc", "usdt"))
            while (true) {
                println(subscription.receive())
            }
        }
        Thread.sleep(1000 * 300)
    }
}