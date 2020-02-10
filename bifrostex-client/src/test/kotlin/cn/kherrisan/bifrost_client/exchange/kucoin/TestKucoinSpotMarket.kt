package cn.kherrisan.bifrost_client.exchange.kucoin

import cn.kherrisan.bifrost_client.common.TestQueryMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName

class TestKucoinSpotMarket : TestQueryMarketMethod() {
    override val name: ExchangeName
        get() = ExchangeName.KUCOIN
}