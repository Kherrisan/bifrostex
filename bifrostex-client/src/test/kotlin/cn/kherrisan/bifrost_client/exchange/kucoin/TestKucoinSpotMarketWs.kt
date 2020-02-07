package cn.kherrisan.bifrost_client.exchange.kucoin

import cn.kherrisan.bifrost_client.common.TestSubscribeMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration
import org.testng.annotations.Test

@Test(enabled = false)
class TestKucoinSpotMarketWs : TestSubscribeMarketMethod() {
    override val name: ExchangeName = ExchangeName.KUCOIN
    override val config: RuntimeConfiguration = RuntimeConfiguration(
            apiKey = "5e29266b284cc10008b74b00",
            secretKey = "6c64792f-911a-47cd-85b3-dcc5c6ac5818",
            password = "zou970514"
    )
}