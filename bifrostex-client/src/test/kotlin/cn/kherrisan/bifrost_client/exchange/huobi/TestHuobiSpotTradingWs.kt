package cn.kherrisan.bifrost_client.exchange.huobi

import cn.kherrisan.bifrost_client.common.TestSubscribeSpotTrading
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestHuobiSpotTradingWs : TestSubscribeSpotTrading() {
    override val name: ExchangeName = ExchangeName.HUOBI

    @Test
    fun `test authentication`() = runBlocking {
        val sub = spotTradingService.subscribeBalance(BTC_USDT)
    }

    @Test
    fun `test subscribe balance of btc-usdt`() = runBlocking {
        val sub = spotTradingService.subscribeBalance(BTC_USDT)
        val job = launch {
            while (true) {
                val spotBalance = sub.receive()
                logger.info(spotBalance)
            }
        }
        delay(20000)
        sub.unsubscribe()
        job.cancel()
        delay(1000)
    }

    @Test
    fun `test subscribe order deal of btc-usdt`() = runBlocking {
        val sub = spotTradingService.subscribeOrderDeal()
        val job = launch {
            while (true) {
                val orderDeal = sub.receive()
                logger.info(orderDeal)
            }
        }
        delay(20000)
        sub.unsubscribe()
        job.cancel()
        delay(1000)
    }
}