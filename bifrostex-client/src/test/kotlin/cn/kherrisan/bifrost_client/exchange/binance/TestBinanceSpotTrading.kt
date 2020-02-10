package cn.kherrisan.bifrost_client.exchange.binance

import cn.kherrisan.bifrost_client.common.TestSpotTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Disabled
import java.time.ZonedDateTime
import java.util.*

class TestBinanceSpotTrading : TestSpotTradingMethod() {
    override val exchangeName: ExchangeName
        get() = ExchangeName.BINANCE
    override val rtConfig: RuntimeConfiguration
        get() = RuntimeConfiguration(
                apiKey = "Mgo20lfO5YJhA9FxXRftjzg1T8TTZfWSk8UO88B3aGjxedEQc3k6E7AThW1WaDep",
                secretKey = "Iomxh91cVZJLsY92KC5cDlrAy6VC2tn5oGPqlLOcH75EvTKakmfVZWerRA6d00wa"
        )

    @Test
    fun testGetBalance() = runBlocking {
        val bal = spotTrading.getBalance()
        bal.entries.forEach { logger.info("${it.key}:${it.value}") }
    }

    /**
     * 测试限价买
     *
     * 下一个超低价的买单
     */
    @Test
    @Disabled
    fun testLimitBuy() {
        runBlocking {
            //{"code":-2010,"msg":"Account has insufficient balance for requested action."}
            logger.info(spotTrading.limitBuy(BTC_USDT, 8500.toBigDecimal(), 0.002.toBigDecimal()))
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
            logger.info(spotTrading.limitSell(BTC_USDT, 12000.toBigDecimal(), 0.001.toBigDecimal()))
        }
    }

    /**
     * 测试市价买
     */
    @Test
    @Disabled
    fun testMarketBuy() {
        runBlocking {
            logger.info(spotTrading.marketBuy(BTC_USDT, 0.0015.toBigDecimal()))
        }
    }

    /**
     * 测试市价卖
     */
    @Test
    @Disabled
    fun testMarketSell() {
        runBlocking {
            logger.info(spotTrading.marketSell(BTC_USDT, 0.0014.toBigDecimal()))
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
            val order = spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.01.toBigDecimal())
            logger.info(order)
            delay(1000)
            logger.info(spotTrading.cancelOrder(order.tid, BTC_USDT))
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
            val order = spotTrading.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.01.toBigDecimal())
            logger.info(order)
            delay(1000)
            logger.info(spotTrading.getOpenOrders(BTC_USDT, 10))
            delay(1000)
            logger.info(spotTrading.cancelOrder(order.tid, BTC_USDT))
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
            //{"code":-1127,"msg":"More than 24 hours between startTime and endTime."}
            //{"code":-1121,"msg":"Invalid symbol."}
            val start = ZonedDateTime.now().minusHours(12)
            val orders = spotTrading.getOrders(BTC_USDT, Date(start.toInstant().toEpochMilli()))
            orders.forEach {
                logger.info(it)
            }
        }
    }

    @Test
    fun testGetOneOrder() {
        runBlocking {
            //oid:1108005821
            logger.info(spotTrading.getOrderDetail("1108005821", BTC_USDT))
        }
    }
}