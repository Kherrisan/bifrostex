package cn.kherrisan.bifrost_client.exchange.huobi

import cn.kherrisan.bifrost_client.common.TestSpotTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.SpringContainer
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.BTC
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.USDT
import cn.kherrisan.bifrostex_client.exchange.huobi.HuobiMetaInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*

class TestHuobiSpotTrading : TestSpotTradingMethod() {
    override val exchangeName: ExchangeName = ExchangeName.HUOBI

    /**
     * 测试限价买
     *
     * 下一个超低价的买单
     */
    @Test
    @Disabled
    fun testLimitBuy() {
        runBlocking {
            logger.debug(spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.02.toBigDecimal()))
        }
    }

    /**
     * 测试限价卖
     *
     * 下一个超高价的卖单
     */
    @Test
    @Disabled
    fun testLimitSell() {
        runBlocking {
            logger.debug(spotTrading.limitSell(BTC_USDT, 12000.toBigDecimal(), 0.004.toBigDecimal()))
        }
    }

    /**
     * 测试市价买
     */
    @Test
    @Disabled
    fun testMarketBuy() {
        runBlocking {
            logger.info(spotTrading.marketBuy(BTC_USDT, volume = 18f.toBigDecimal()))
            //66773470708
        }
    }

    /**
     * 测试市价卖
     */
    @Test
    @Disabled
    fun testMarketSell() {
        runBlocking {
            logger.info(spotTrading.marketSell(BTC_USDT, 0.002.toBigDecimal()))
            //66773402413
        }
    }

    /**
     * 测试取消订单
     *
     * 先下一个超高价的限价卖单，然后取消他
     */
    @Test
    @Disabled
    fun testCancelOrder() {
        runBlocking {
            val order = spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.02.toBigDecimal())
            logger.debug(order)
            delay(1000)
            logger.debug(spotTrading.cancelOrder(order.tid, BTC_USDT))
        }
    }

    /**
     * 测试获得所有未完成订单
     *
     * 先下一个超高价限价卖单，然后查询
     */
    @Test
    @Disabled
    fun testGetOpenOrders() {
        runBlocking {
            val order = spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.02.toBigDecimal())
            logger.debug(order)
            delay(1000)
            logger.debug(spotTrading.getOpenOrders(BTC_USDT, 10))
        }
    }

    /**
     * 测试获得手续费
     */
    @Test
    fun testGetFees() {
        runBlocking {
            logger.info(spotTrading.getFee(BTC_USDT))
        }
    }

    @Test
    fun testGetOrder() = runBlocking {
        val metaInfo = SpringContainer[HuobiMetaInfo::class.java]
        val o = spotTrading.getOrderDetail("66773470708", BTC_USDT)
        logger.info(o)
        val meta = metaInfo.symbolMetaInfo[BTC_USDT]!!
        // 检查几个报价精度和数量精度
        assert(o.amount.scale() == meta.sizeIncrement)
        assert(o.filledAmount.scale() == meta.sizeIncrement)
        assert(o.price.scale() == meta.priceIncrement)
    }

    /**
     * 测试查询历史订单
     */
    @Test
    fun testGetOrders() {
        runBlocking {
            val start = ZonedDateTime.now().minusDays(1)
            val orders = spotTrading.getOrders(Symbol(BTC, USDT), Date(start.toInstant().toEpochMilli()), state = OrderStateEnum.FILLED)
            for (o in orders) {
                logger.info(o)
            }
        }
    }

    /**
     * 测试转账到逐仓杠杆账户
     */
    @Test
    @Disabled
    fun testTransferToMargin() = runBlocking {
        logger.info(spotTrading.transferToMargin(USDT, 0.6.toBigDecimal(), BTC_USDT))
        // {"status":"error","err-code":"dw-insufficient-balance","err-msg":"Insufficient balance. You can only transfer `0.00486024` at most.","data":null}
    }

    @Test
    fun testGetBalance() = runBlocking {
        val bal = spotTrading.getBalance()
        bal.entries.forEach { logger.info("${it.key}:${it.value}") }
    }
}