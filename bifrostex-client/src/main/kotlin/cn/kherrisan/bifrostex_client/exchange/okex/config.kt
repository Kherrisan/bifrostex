package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class OkexMetaInfo : ExchangeMetaInfo()

@ConfigurationProperties(prefix = "bifrostex.exchange.huobi")
@Configuration
class OkexRuntimeConfig : ExchangeRuntimeConfig()

@Component
class OkexStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://www.okex.com"
    override var spotTradingHttpHost: String = "https://www.okex.com"
    override var spotMarketWsHost: String = "wss://real.okex.com:8443/ws/v3"
}