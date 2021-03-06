package cn.kherrisan.bifrost_client.exchange.huobi

import cn.kherrisan.bifrost_client.common.TestMarginTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.SpringContainer
import cn.kherrisan.bifrostex_client.entity.BTC
import cn.kherrisan.bifrostex_client.entity.BTC_USDT
import cn.kherrisan.bifrostex_client.entity.USDT
import cn.kherrisan.bifrostex_client.exchange.huobi.HuobiMetaInfo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestHuobiMarginTrading : TestMarginTradingMethod() {

    override val exchangeName: ExchangeName = ExchangeName.HUOBI

    @Test
    @Disabled
    fun testTransfetToSpot() = runBlocking {
        logger.info(marginTradingService.transferToSpot(BTC, 0.01f.toBigDecimal(), BTC_USDT))
        //{"status":"error","err-code":"dw-insufficient-balance","err-msg":"Insufficient balance. You can only transfer `0.01` at most.","data":null}
    }

    @Test
    fun testGetLoanOrder() = runBlocking {
        val metaInfo = SpringContainer[HuobiMetaInfo::class.java]
        val orders = marginTradingService.getLoanOrders(BTC_USDT)
        orders.forEach {
            logger.info(it)
            val meta = metaInfo.currencyMetaInfo[it.currency]!!
            assert(it.amount.scale() == meta.sizeIncrement)
            assert(it.interest.scale() == meta.sizeIncrement)
        }
    }


    @Test
    fun testGetLoanBalance() = runBlocking {
        val metaInfo = SpringContainer[HuobiMetaInfo::class.java]
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
        mi.forEach { t, u ->
            logger.info(u)
        }
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
    }

    @Test
    @Disabled
    fun testRepayLoanedBTC() = runBlocking {
        val r = marginTradingService.repay("5952759", USDT, BTC_USDT, 0.000083.toBigDecimal())
        logger.info(r)
        // 超额还款：{"status":"error","err-code":"loan-repay-max-limit","err-msg":"This amount exceeds the Unsettled amount. Please re-enter.","data":null}
    }
}