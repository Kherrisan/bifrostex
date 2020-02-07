package cn.kherrisan.bifrost_client.exchange.gateio

import cn.kherrisan.bifrost_client.common.GROUP_GATEIO
import cn.kherrisan.bifrost_client.common.SUIT_SPOT_MARKET_WS_METHOD
import cn.kherrisan.bifrost_client.common.TestSubscribeMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import com.aventstack.extentreports.testng.listener.ExtentIReporterSuiteClassListenerAdapter
import org.testng.annotations.Listeners
import org.testng.annotations.Test

@Test(groups = [GROUP_GATEIO, SUIT_SPOT_MARKET_WS_METHOD], enabled = false)
@Listeners(ExtentIReporterSuiteClassListenerAdapter::class)
class TestGateioMarketWs : TestSubscribeMarketMethod() {
    override val name: ExchangeName = ExchangeName.GATEIO
}