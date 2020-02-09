package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.stereotype.Component

@Component
class HuobiStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://api.huobi.pro"
    override var spotTradingHttpHost: String = "https://api.huobi.pro"
    override var spotMarketWsHost: String = "wss://api.huobi.pro/ws"
    override var spotTradingWsHost: String = "wss://api.huobi.pro/ws/v1"
}