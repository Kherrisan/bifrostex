package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "bifrostex.exchange.kucoin")
@Configuration
class KucoinRuntimeConfig : ExchangeRuntimeConfig()

@Component
class KucoinStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://api.kucoin.com"
}

@Component
class KucoinMetaInfo : ExchangeMetaInfo()