package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx

class GateioService(vertx: Vertx) : ExchangeService(vertx) {
    override var publicHttpHost: String = "https://data.gateio.life"
    override var publicWsHost: String = "wss://ws.gate.io/v3/"

    override fun buildWebsocketDispatcher(): WebsocketDispatcher {
        return GateioWebsocketDispatcher(this)
    }

    override fun buildSpotMarketService(): SpotMarketService {
        return GateioSpotMarketService(this)
    }

    override fun buildDataAdaptor(): ServiceDataAdaptor {
        return GateioServiceDataAdaptor(this)
    }
}