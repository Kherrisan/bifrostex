package cn.kherrisan.bifrost_client.exchange.okex

import cn.kherrisan.bifrost_client.common.TestMarginTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.enumeration.LoanStatusEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.USDT
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*

class TestOkexMarginTrading : TestMarginTradingMethod() {
    override val exchangeName: ExchangeName = ExchangeName.OKEX

    @Test
    @Disabled
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

    @Test
    @Disabled
    fun testLoanBTC() = runBlocking {
        val mi = marginTradingService.getMarginInfo()[BTC_USDT]!!
        val min = mi.quote.minLoanableAmount
        val loanAmt = min.max(mi.quote.loanableAmount / 10f.toBigDecimal())
        logger.info("Loan USDT: ${loanAmt}")
        val r = marginTradingService.loan(USDT, loanAmt, BTC_USDT)
        logger.info(r)
        //{"code":33004,"message":"The number of borrowed coins cannot be less than the minimum number of borrowed coins"}
    }

    @Test
    @Disabled
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

    @Test
    @Disabled
    fun testMarketBuySpot() = runBlocking {
        logger.info(marginTradingService.marketBuy(BTC_USDT, volume = 66.toBigDecimal()))
        //{"client_oid":"","error_code":"33017","error_message":"Greater than the maximum available balance","order_id":"-1","result":false}
    }

    @Test
    @Disabled
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
    @Test
    @Disabled
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
    @Test
    @Disabled
    fun testGetOpenOrders() {
        runBlocking {
            val order = marginTradingService.limitBuy(BTC_USDT, 2000.toBigDecimal(), 0.02.toBigDecimal())
            logger.info(order)
            delay(1000)
            logger.info(marginTradingService.getOpenOrders(BTC_USDT, 10))
        }
    }

    @Test
    @Disabled
    fun testGetSpecifiedOrder() = runBlocking {
        logger.info(marginTradingService.getOrderDetail("4308536683926528", BTC_USDT))
    }
}