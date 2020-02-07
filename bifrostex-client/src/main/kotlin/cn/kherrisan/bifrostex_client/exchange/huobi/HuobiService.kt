package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.enumeration.AccountTypeEnum
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx

class HuobiService(vertx: Vertx) : ExchangeService(vertx) {
    override var publicHttpHost = "https://api.huobi.pro"
    override var publicWsHost = "wss://api.huobi.pro/ws"
    override var authHttpHost = "https://api.huobi.pro"

    lateinit var accountIdMap: Map<AccountTypeEnum, String>

    override fun buildAuthenticationService(): AuthenticationService {
        return HuobiAuthenticateService(this)
    }

    override fun buildWebsocketDispatcher(): WebsocketDispatcher {
        return HuobiWebsocketDispatcher(this)
    }

    override fun buildDataAdaptor(): ServiceDataAdaptor {
        return HuobiServiceDataAdaptor(this)
    }

    override fun buildSpotMarketService(): SpotMarketService {
        return HuobiSpotMarketService(this)
    }

    override fun buildSpotTradingService(): SpotTradingService {
        return HuobiSpotTradingService(this)
    }

    override fun buildMarginTradingService(): MarginTradingService {
        return HuobiMarginTradingService(this)
    }
}