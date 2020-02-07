package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.*
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx

class KucoinService(vertx: Vertx) : ExchangeService(vertx) {
    override var publicHttpHost: String = "https://api.kucoin.com"
    override var publicWsHost: String = ""
    protected val sandbox = "https://openapi-sandbox.kucoin.com"

    /**
     * 初始化工作
     *
     * 1. 获得publicWsHost
     * 2. 获得authWsHost
     */
    override suspend fun allInitialize() {
        val instance = (spotMarketService as KucoinSpotMarketService).getInstanceServer()
        publicWsHost = "${instance.url}?token=${instance.token}&connectId=${md5(uuid())}&acceptUserMessage=true"
        rtConfig.pingTimeout = instance.pingTimeout
        rtConfig.pingInterval = instance.pingInterval
        val privateInstance = (spotMarketService as KucoinSpotMarketService).getPrivateInstanceServer()
        authWsHost = "${privateInstance.url}?token=${privateInstance.token}&connectId=${md5(uuid())}&acceptUserMessage=true"
    }

    override fun buildWebsocketDispatcher(): WebsocketDispatcher {
        return KucoinWebsocketDispatcher(this)
    }

    override fun buildSpotTradingService(): SpotTradingService {
        return KucoinSpotTradingService(this)
    }

    override fun buildSpotMarketService(): SpotMarketService {
        return KucoinSpotMarketService(this)
    }

    override fun buildDataAdaptor(): ServiceDataAdaptor {
        return KucoinSerivceDataAdaptor(this)
    }
}