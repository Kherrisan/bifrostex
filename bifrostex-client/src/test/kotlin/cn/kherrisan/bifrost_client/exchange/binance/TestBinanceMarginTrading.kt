package cn.kherrisan.bifrost_client.exchange.binance

import cn.kherrisan.bifrost_client.common.TestMarginTradingMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration

class TestBinanceMarginTrading : TestMarginTradingMethod() {
    override val exchangeName: ExchangeName
        get() = TODO("Init the exchangeName")
    override val rtConfig: RuntimeConfiguration
        get() = super.rtConfig
}