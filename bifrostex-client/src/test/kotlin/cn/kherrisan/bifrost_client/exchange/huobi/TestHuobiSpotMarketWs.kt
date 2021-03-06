package cn.kherrisan.bifrost_client.exchange.huobi

import cn.kherrisan.bifrost_client.common.TestSubscribeMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.Depth
import cn.kherrisan.bifrostex_client.entity.Kline
import cn.kherrisan.bifrostex_client.entity.Ticker
import cn.kherrisan.bifrostex_client.entity.Trade
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestHuobiSpotMarketWs : TestSubscribeMarketMethod() {

    override val name: ExchangeName = ExchangeName.HUOBI

    @Test
    fun testSubscribeDepth() = runBlocking {
        val depthList = mutableListOf<Depth>()
        val sub = spotMarketService.subscribeDepth(symbol)
        val l = launch {
            while (true) {
                val depth = sub.receive()
                val minAsk = depth.asks.last()
                val maxBid = depth.bids.first()
                logger.info("Min ask: $minAsk, max bid: $maxBid")
                assert(minAsk.price > maxBid.price)
                assert(minAsk.price.scale() == symbolMetaInfo.priceIncrement)
                assert(maxBid.amount.scale() == symbolMetaInfo.sizeIncrement)
                depthList.add(depth)
            }
        }
        delay(20_000)
        // 因为 Huobi 的 Depth 订阅的全量数据可能会出错，多次尝试全量获取获取会浪费时间，30 秒内必须受到有效数据
        assert(depthList.size > 0)
        sub.unsubscribe()
        l.cancel()
        logger.debug("sub.unsubscribe()")
    }

    @Test
    fun testSubscribeTrade() = runBlocking(vertx.dispatcher()) {
        val tradeList = mutableListOf<Trade>()
        val sub = spotMarketService.subscribeTrade(symbol)
        val l = launch {
            while (true) {
                val trade = sub.receive()
                logger.info("$trade")
                tradeList.add(trade)
            }
        }
        delay(10_000)
        sub.unsubscribe()
        l.cancel()
        delay(1000)
    }

    @Test
    fun testSubscribeKline() = runBlocking(vertx.dispatcher()) {
        val klineList = mutableListOf<Kline>()
        val sub = spotMarketService.subscribeKline(symbol)
        val l = launch {
            while (true) {
                val kline = sub.receive()
                logger.info("$kline")
                klineList.add(kline)
            }
        }
        delay(10_000)
        sub.unsubscribe()
        l.cancel()
        delay(1000)
    }

    @Test
    fun testSubscribeTicker() = runBlocking(vertx.dispatcher()) {
        val tickerList = mutableListOf<Ticker>()
        val sub = spotMarketService.subscribeTicker(symbol)
        val l = launch {
            while (true) {
                val ticker = sub.receive()
                assert(ticker.open.scale() == symbolMetaInfo.priceIncrement)
                assert(ticker.high.scale() == symbolMetaInfo.priceIncrement)
                assert(ticker.vol.scale() == symbolMetaInfo.volumeIncrement)
                assert(ticker.amount.scale() == symbolMetaInfo.sizeIncrement)
                logger.info("$ticker")
                tickerList.add(ticker)
            }
        }
        delay(10_000)
        sub.unsubscribe()
        l.cancel()
        delay(1000)
    }
}