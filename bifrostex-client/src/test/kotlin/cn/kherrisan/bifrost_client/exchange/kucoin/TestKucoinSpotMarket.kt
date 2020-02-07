package cn.kherrisan.bifrost_client.exchange.kucoin

import cn.kherrisan.bifrost_client.common.TestQueryMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import org.testng.annotations.Test

@Test(enabled = false)
class TestKucoinSpotMarket : TestQueryMarketMethod() {
    override val name: ExchangeName
        get() = ExchangeName.KUCOIN
}