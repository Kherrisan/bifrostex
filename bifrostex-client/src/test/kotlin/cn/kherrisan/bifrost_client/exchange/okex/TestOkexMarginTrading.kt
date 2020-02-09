package cn.kherrisan.bifrost_client.exchange.okex

import cn.kherrisan.bifrost_client.common.GROUP_OKEX
import cn.kherrisan.bifrost_client.common.SUIT_MARGIN_TRADING_METHOD
import cn.kherrisan.bifrost_client.common.TestMarginTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration
import cn.kherrisan.bifrostex_client.core.enumeration.LoanStatusEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.USDT
import com.aventstack.extentreports.testng.listener.ExtentIReporterSuiteClassListenerAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.testng.annotations.Listeners
import org.testng.annotations.Test
import java.time.ZonedDateTime
import java.util.*

@Test(groups = [GROUP_OKEX, SUIT_MARGIN_TRADING_METHOD])
@Listeners(ExtentIReporterSuiteClassListenerAdapter::class)
class TestOkexMarginTrading : TestMarginTradingMethod() {
    override val exchangeName: ExchangeName = ExchangeName.OKEX
    override val rtConfig: RuntimeConfiguration = RuntimeConfiguration(
            apiKey = "abc30b71-f30f-4a17-aa3c-5470a8cc2f79",
            secretKey = "8AFAEB40EB6F7D89363741BAD64476A3",
            password = "Q4y4VghDdsCqR3ZR")

    @Test(enabled = false)
    fun testTransfetToSpot() = runBlocking {
        logger.info(marginTradingService.transferToSpot(USDT, 88f.toBigDecimal(), BTC_USDT))
        //{"status":"error","err-code":"dw-insufficient-balance","err-msg":"Insufficient balance. You can only transfer `0` at most.","data":null}
    }

    @Test
    fun testGetLoanOrder() = runBlocking {
        val orders = marginTradingService.getLoanOrders(BTC_USDT, status = LoanStatusEnum.REPAYED)
        orders.forEach {
            logger.info(it)
            val meta = metaInfo.currencyMetaInfo[it.currency]!!
            assert(it.amount.scale() == meta.sizeIncrement)
            assert(it.interest.scale() == meta.sizeIncrement)
        }
    }


    @Test
    fun testGetLoanBalance() = runBlocking {
        val bal = marginTradingService.getBalance()
        bal.forEach {
            logger.info(it.value)
            val sym = it.key
            val balance = it.value
            val meta = metaInfo
            assert(balance.flatPrice.scale() == meta.symbolMetaInfo[sym]!!.priceIncrement)
            val base = balance.base
            assert(base.available.scale() == meta.currencyMetaInfo[base.currency]!!.sizeIncrement)
            val quote = balance.quote
            assert(quote.frozen.scale() == meta.currencyMetaInfo[quote.currency]!!.sizeIncrement)
            assert(quote.interest.scale() == meta.currencyMetaInfo[quote.currency]!!.sizeIncrement)
        }
    }

    @Test
    fun testGetMarginInfo() = runBlocking {
        val mi = marginTradingService.getMarginInfo()
//        mi.forEach { t, u ->
//            logger.info(u)
//        }
        logger.info(mi[BTC_USDT]!!.quote)
    }

    @Test(enabled = false)
    fun testLoanBTC() = runBlocking {
        val mi = marginTradingService.getMarginInfo()[BTC_USDT]!!
        val min = mi.quote.minLoanableAmount
        val loanAmt = min.max(mi.quote.loanableAmount / 10f.toBigDecimal())
        logger.info("Loan USDT: ${loanAmt}")
        val r = marginTradingService.loan(USDT, loanAmt, BTC_USDT)
        logger.info(r)
        //{"code":33004,"message":"The number of borrowed coins cannot be less than the minimum number of borrowed coins"}
    }

    @Test(enabled = false)
    fun testRepayLoanedBTC() = runBlocking {
        val r = marginTradingService.repay("5952759", USDT, BTC_USDT, 0.000083.toBigDecimal())
        logger.info(r)
    }

    @Test
    fun testLoanOverMaxBTC() = runBlocking {

    }

    @Test
    fun testReayOverLoanedBTC() {

    }

    @Test(enabled = false)
    fun testMarketBuySpot() = runBlocking {
        logger.info(marginTradingService.marketBuy(BTC_USDT, volume = 66.toBigDecimal()))
        //{"client_oid":"","error_code":"33017","error_message":"Greater than the maximum available balance","order_id":"-1","result":false}
    }

    @Test(enabled = false)
    fun testMarketSellSpot() = runBlocking {
        logger.info(marginTradingService.marketSell(BTC_USDT, 0.007.toBigDecimal()))
    }

    /**
     * 测试查询历史订单
     */
    @Test
    fun testGetOrders() {
        runBlocking {
            val start = ZonedDateTime.now().minusDays(1)
            val orders = marginTradingService.getOrders(BTC_USDT, Date(start.toInstant().toEpochMilli()), state = OrderStateEnum.FILLED)
            for (o in orders) {
                logger.info(o)
            }
        }
    }

    /**
     * 测试取消订单
     *
     * 先下一个超高价的限价卖单，然后取消他
     */
    @Test(enabled = false)
    fun testCancelOrder() {
        runBlocking {
            val order = marginTradingService.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.01.toBigDecimal())
            logger.info(order)
            delay(1000)
            logger.info(marginTradingService.cancelOrder(order.tid, BTC_USDT))
        }
    }

    /**
     * 测试获得所有未完成订单
     *
     * 先下一个超高价限价卖单，然后查询
     */
    @Test(enabled = false)
    fun testGetOpenOrders() {
        runBlocking {
            val order = marginTradingService.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.02.toBigDecimal())
            logger.info(order)
            delay(1000)
            logger.info(marginTradingService.getOpenOrders(BTC_USDT, 10))
        }
    }

    @Test(enabled = false)
    fun testGetSpecifiedOrder() = runBlocking {
        logger.info(marginTradingService.getOrderDetail("4308536683926528", BTC_USDT))
    }
}