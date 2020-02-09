package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import org.springframework.stereotype.Component

@Component
class GateioStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://data.gateio.life"
    override var spotMarketWsHost: String = "wss://ws.gate.io/v3/"
}