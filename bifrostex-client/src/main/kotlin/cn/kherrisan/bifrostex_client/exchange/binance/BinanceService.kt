package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx

class BinanceService(vertx: Vertx) : ExchangeService(vertx) {
    override var publicHttpHost: String = "https://api.binance.com"
    override var publicWsHost: String = "wss://stream.binance.com:9443/ws/stream1"
    override var authHttpHost: String = "https://api.binance.com"

    override fun buildAuthenticationService(): AuthenticationService {
        return BinanceAuthenticateService(this)
    }

    override fun buildWebsocketDispatcher(): WebsocketDispatcher {
        return BinanceWebsocketDispatcher(this)
    }

    override fun buildSpotMarketService(): SpotMarketService {
        return BinanceSpotMarketService(this)
    }

    override fun buildSpotTradingService(): SpotTradingService {
        return BinanceSpotTradingService(this)
    }

    override fun buildDataAdaptor(): ServiceDataAdaptor {
        return BinanceServiceDataAdaptor(this)
    }

    override fun buildMarginTradingService(): MarginTradingService {
        return BinanceMarginTradingService(this)
    }
}