package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx

class OkexService(vertx: Vertx) : ExchangeService(vertx) {
    override var publicHttpHost: String = "https://www.okex.com"
    override var publicWsHost: String = "wss://real.okex.com:8443/ws/v3"
    override var authHttpHost: String = "https://www.okex.com"

    override fun buildAuthenticationService(): AuthenticationService {
        return OkexAuthenticateService(this)
    }

    override fun buildSpotTradingService(): SpotTradingService {
        return OkexSpotTradingService(this)
    }

    override fun buildWebsocketDispatcher(): WebsocketDispatcher {
        return OkexWebsocketDispatcher(this)
    }

    override fun buildSpotMarketService(): SpotMarketService {
        return OkexSpotMarketService(this)
    }

    override fun buildDataAdaptor(): ServiceDataAdaptor {
        return OkexServiceDataAdaptor(this)
    }

    override fun buildMarginTradingService(): MarginTradingService {
        return OkexMarginTradingService(this)
    }
}