package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration

class KucoinStaticConfiguration : ExchangeStaticConfiguration() {
    override var spotMarketHttpHost: String = "https://api.kucoin.com"
}