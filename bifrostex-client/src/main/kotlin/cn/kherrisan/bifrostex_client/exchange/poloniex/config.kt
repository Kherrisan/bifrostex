package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "bifrostex.exchange.poloniex")
@Configuration
class PoloniexRuntimeConfig : ExchangeRuntimeConfig()

@Component
class PoloniexStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://poloniex.com/public"
    override var spotMarketWsHost: String = "wss://api2.poloniex.com"
}

@Component
class PoloniexMetaInfo : ExchangeMetaInfo()