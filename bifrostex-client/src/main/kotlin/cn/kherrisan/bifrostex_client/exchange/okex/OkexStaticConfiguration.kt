package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.stereotype.Component

@Component
class OkexStaticConfiguration:ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://www.okex.com"
    override var spotTradingHttpHost: String = "https://www.okex.com"
    override var spotMarketWsHost: String = "wss://real.okex.com:8443/ws/v3"
}