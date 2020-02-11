package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class BinanceMetaInfo : ExchangeMetaInfo()

@Component
class BinanceStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://api.binance.com"
    override var spotTradingHttpHost: String = "https://api.binance.com"
    override var spotMarketWsHost: String = "wss://stream.binance.com:9443/ws/stream1"
}

@ConfigurationProperties(prefix = "bifrostex.exchange.binance")
@Configuration
class BinanceRuntimeConfig : ExchangeRuntimeConfig()