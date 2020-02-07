package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx

class PoloniexService(vertx: Vertx) : ExchangeService(vertx) {
    override var publicHttpHost: String = "https://poloniex.com/public"
    override var publicWsHost: String = "wss://api2.poloniex.com"

    override fun buildWebsocketDispatcher(): WebsocketDispatcher {
        return PoloniexWebsocketDispatcher(this)
    }

    override fun buildSpotMarketService(): SpotMarketService {
        return PoloniexSpotMarketService(this)
    }

    override fun buildDataAdaptor(): ServiceDataAdaptor {
        return PoloniexServiceDataAdaptor(this)
    }
}