package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration

class PoloniexStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://poloniex.com/public"
    override var spotMarketWsHost: String = "wss://api2.poloniex.com"
}