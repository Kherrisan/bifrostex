package cn.kherrisan.bifrost_client.exchange.okex

import cn.kherrisan.bifrost_client.common.GROUP_OKEX
import cn.kherrisan.bifrost_client.common.SUIT_SPOT_TRADING_METHOD
import cn.kherrisan.bifrost_client.common.TestSpotTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.USDT
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Disabled
import java.time.ZonedDateTime
import java.util.*

class TestOkexSpotTrading : TestSpotTradingMethod() {
    override val exchangeName: ExchangeName
        get() = ExchangeName.OKEX
    override val rtConfig: RuntimeConfiguration
        get() = RuntimeConfiguration(apiKey = "abc30b71-f30f-4a17-aa3c-5470a8cc2f79",
                secretKey = "8AFAEB40EB6F7D89363741BAD64476A3",
                password = "Q4y4VghDdsCqR3ZR")

    /**
     * 测试限价买
     *
     * 下一个超低价的买单
     */
    @Test
    @Disabled
    fun testLimitBuy() {
        runBlocking {
            //oid:4250082321523712
            logger.debug(spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.005.toBigDecimal()))
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
            //oid:4250089668481025
            logger.debug(spotTrading.limitSell(BTC_USDT, 12000.toBigDecimal(), 0.003.toBigDecimal()))
        }
    }

    /**
     * 测试市价买
     */
    @Test
    @Disabled
    fun testMarketBuy() {
        runBlocking {
            //oid:4250095537891328
            logger.debug(spotTrading.marketBuy(BTC_USDT, 20.toBigDecimal()))
        }
    }

    /**
     * 测试市价卖
     */
    @Test
    @Disabled
    fun testMarketSell() {
        runBlocking {
            //oid:4250102127403008
            logger.debug(spotTrading.marketSell(BTC_USDT, 0.002.toBigDecimal()))
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
            val order = spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.005.toBigDecimal())
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
            val order = spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.005.toBigDecimal())
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

    /**
     * 测试查询历史订单
     */
    @Test
    fun testGetOrders() {
        runBlocking {
            val start = ZonedDateTime.now().minusDays(1)
            val orders = spotTrading.getOrders(BTC_USDT, Date(start.toInstant().toEpochMilli()), state = OrderStateEnum.FILLED)
            for (o in orders) {
                logger.info(o)
            }
        }
    }

    @Test
    @Disabled
    fun testTransferToMarginAccount() = runBlocking {
        logger.info(spotTrading.transferToMargin(USDT, 68.toBigDecimal(), BTC_USDT))
        //超额划转：{"code":34008,"message":"Insufficient funds"}
    }
}