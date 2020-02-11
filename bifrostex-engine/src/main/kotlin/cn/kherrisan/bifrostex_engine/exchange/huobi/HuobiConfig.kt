package cn.kherrisan.bifrostex_engine.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "bifrostex.exchange.huobi")
@Configuration
class HuobiConfig : ExchangeRuntimeConfig() {
}