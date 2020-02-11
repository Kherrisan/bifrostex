package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "bifrostex.exchange.gateio")
@Configuration
class GateioRuntimeConfig : ExchangeRuntimeConfig()

@Component
class GateioStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://data.gateio.life"
    override var spotMarketWsHost: String = "wss://ws.gate.io/v3/"
}