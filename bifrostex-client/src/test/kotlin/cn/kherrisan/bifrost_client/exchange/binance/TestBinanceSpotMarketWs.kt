package cn.kherrisan.bifrost_client.exchange.binance

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

class TestBinanceSpotMarketWs : TestSubscribeMarketMethod() {
    override val name: ExchangeName = ExchangeName.BINANCE

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
        delay(30_000)
        l.cancel()
        // 因为 Huobi 的 Depth 订阅的全量数据可能会出错，多次尝试全量获取获取会浪费时间，30 秒内必须受到有效数据
        assert(depthList.size > 0)
        sub.unsubscribe()
        logger.debug("sub.unsubscribe()")
    }

    @Test
    fun testSubscribeTrade() = runBlocking(vertx.dispatcher()) {
        val tradeList = mutableListOf<Trade>()
        val sub = spotMarketService.subscribeTrade(symbol)
        val l = launch {
            while (true) {
                val trade = sub.receive()
                assert(trade.price.scale() == symbolMetaInfo.priceIncrement)
                assert(trade.amount.scale() == symbolMetaInfo.sizeIncrement)
                logger.info("$trade")
                tradeList.add(trade)
            }
        }
        delay(30_000)
        l.cancel()
        sub.unsubscribe()
        delay(1000)
    }

    @Test
    fun testSubscribeKline() = runBlocking(vertx.dispatcher()) {
        val klineList = mutableListOf<Kline>()
        val sub = spotMarketService.subscribeKline(symbol)
        val l = launch {
            while (true) {
                val kline = sub.receive()
                assert(kline.volume.scale() == symbolMetaInfo.sizeIncrement)
                assert(kline.open.scale() == symbolMetaInfo.priceIncrement)
                assert(kline.high.scale() == symbolMetaInfo.priceIncrement)
                logger.info("$kline")
                klineList.add(kline)
            }
        }
        delay(30_000)
        l.cancel()
        sub.unsubscribe()
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
        delay(30_000)
        l.cancel()
        sub.unsubscribe()
        delay(1000)
    }
}