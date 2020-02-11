package cn.kherrisan.bifrost_client.exchange.okex

import cn.kherrisan.bifrost_client.common.TestQueryMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.SpringContainer
import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.exchange.okex.OkexMetaInfo
import cn.kherrisan.bifrostex_client.exchange.okex.OkexRuntimeConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import kotlin.random.Random

class TestOkexSpotMarket : TestQueryMarketMethod() {
    override val name: ExchangeName = ExchangeName.OKEX

    @Autowired
    override lateinit var rtConfig: OkexRuntimeConfig

    @Test
    fun testGetSymbolMetaInfo() = runBlocking {
        val metaInfo = spotMarketService.getSymbolMetaInfo()
        logger.debug(metaInfo)
        assert(metaInfo.size > 10)
    }

    @Test
    fun getAllSymbols() = runBlocking {
        val symbols = spotMarketService.getSymbols()
        logger.debug(symbols)
        // 检查是否有足够多的symbols
        assert(symbols.size > 10)
        // 检查是否有重复的basequote字符串
        assert(symbols.map { "${it.base}${it.quote}" }
                .distinct().size
                ==
                symbols.size)
    }

    @Test
    fun testGetAllCurrencys() = runBlocking {
        val currencyList = spotMarketService.getCurrencies()
        logger.debug(currencyList)
        // 检查是否有足够多的currencys
        assert(currencyList.size > 10) { "currencyList.size > 10" }
        // 检查是否有重复的currency
        assert(currencyList.distinct().size == currencyList.size) { "currencyList.distinct().size == currencyList.size" }
        // 检查每个currency名字的字符长度是否在正常范围内
        currencyList.forEach {
            assert(it.name.isNotEmpty())
            assert(it.name.length <= 10)
        }
    }

    @Test
    fun testGetTickerOfUnknown() = runBlocking {
        val UNKNOWN = Symbol("dd", "aa")
        try {
            val ticker = spotMarketService.getTicker(UNKNOWN)
        } catch (e: Exception) {
            //{"code":30032,"message":"The currency pair does not exist"}
            logger.debug(e.message)
        }
    }

    @Test
    fun testGetTickerOfBTCUSDT() = runBlocking {
        val ticker = spotMarketService.getTicker(BTC_USDT)
        logger.debug(ticker)
        // 检查最高价是否高于最低价
        assert(ticker.high > ticker.low) { "ticker.high > ticker.low" }
        // 检查买价是否大于0
        assert(ticker.bid > BigDecimal.ZERO) { "ticker.bid > BigDecimal.ZERO" }
        // 检查卖价是否大于0（有些交易所的Ticker中不包含ask和bid价格，进行这个测试的目的是为了规范ticker数据）
        assert(ticker.ask > BigDecimal.ZERO) { "ticker.ask > BigDecimal.ZERO" }
        // 检查amt和vol的关系
        assert(ticker.vol < ticker.amount * ticker.high) { "ticker.vol < ticker.amount * ticker.high" }
        assert(ticker.vol > ticker.amount * ticker.low) { "ticker.vol > ticker.amount * ticker.low" }
        // 检查 high、low、amount、vol 的 scale 是否符合 metaInfo 的要求
        val meta = metaInfo.symbolMetaInfo[BTC_USDT]!!
        assert(ticker.high.scale() == meta.priceIncrement) { "ticker.high.scale() == meta.priceIncrement" }
        assert(ticker.low.scale() == meta.priceIncrement) { "ticker.low.scale() == meta.priceIncrement" }
        assert(ticker.amount.scale() == meta.sizeIncrement) { "ticker.amount.scale() == meta.sizeIncrement" }
        assert(ticker.vol.scale() == meta.volumeIncrement) { "ticker.vol.scale() == meta.volumeIncrement" }
    }


    @Test
    fun getDepthForSth() = runBlocking {
        val depth = spotMarketService.getDepths(BTC_USDT, 20)
        val meta = metaInfo.symbolMetaInfo[BTC_USDT]!!
        logger.debug(depth)
        // 检查最高卖价是否高于最低买价
        val minAsk = depth.asks.last()
        val maxBid = depth.bids.first()
        if (minAsk != null) {
            assert(minAsk > maxBid)
        }
        // 检查askmap和bidmap是否同时为空
        assert(!(depth.asks.isEmpty() && depth.bids.isEmpty()))
        // 检查 size
        assert(depth.asks.size == 20)
        assert(depth.bids.size == 20)
        var last: BigDecimal? = null
        for (ask in depth.asks) {
            if (last == null) {
                last = ask.price
            } else {
                // 检查 asks 和 bids 是否递减
                assert(last > ask.price)
                assert(ask.price.scale() == meta.priceIncrement)
            }
        }
        for (bid in depth.bids) {
            if (last == null) {
                last = bid.price
            } else {
                // 检查 asks 和 bids 是否递减
                assert(last > bid.price)
                // 检查报价精度
                assert(bid.price.scale() == meta.priceIncrement)
            }
        }
    }

    @Test
    fun getTradesForSth() = runBlocking {
        val trades = spotMarketService.getTrades(BTC_USDT, 15)
        logger.debug(trades)
        // 检查trade的symbol
        trades.forEach { assert(it.symbol == symbol) }
        val meta = metaInfo.symbolMetaInfo[BTC_USDT]!!
        // 检查trade是否按照time的递增顺序排列
        var last: Date? = null
        trades.forEach {
            last?.let { l -> assert(l <= it.time) }
            last = it.time
            assert(it.amount.scale() == meta.sizeIncrement)
            assert(it.price.scale() == meta.priceIncrement)
        }
    }


    @Test
    fun getKlinesFotSth() = runBlocking {
        val metaInfo = SpringContainer[OkexMetaInfo::class.java]
        val startTime = ZonedDateTime.now().minusMonths(1)
        val size = Random.nextInt(5, 20)
        // 测试一个月之前的数据
        val klines = spotMarketService.getKlines(BTC_USDT, KlinePeriodEnum._1DAY, size, Date(startTime.toInstant().toEpochMilli()))
        logger.debug(klines)
        val meta = metaInfo.symbolMetaInfo[BTC_USDT]!!
        // 检查kline数量
        assert(klines.size == size || klines.size == size + 1)
        // 检查精度
        klines.forEach {
            assert(it.open.scale() == meta.priceIncrement)
            assert(it.close.scale() == meta.priceIncrement)
            assert(it.high.scale() == meta.priceIncrement)
            assert(it.low.scale() == meta.priceIncrement)
            assert(it.volume.scale() == meta.volumeIncrement)
        }
        // 检查kline的date的递增顺序
        var last: Date? = null
        for (t in klines.map { it.time }) {
            if (last != null) {
                assert(last < t)
            }
            last = t
        }
        val firstTime = klines[0].time.toInstant()
        val lastTime = klines.last().time.toInstant()
        // 最后一个K线的时间要在当前时间之前
        assert(lastTime.isBefore(Instant.now()))
    }
}