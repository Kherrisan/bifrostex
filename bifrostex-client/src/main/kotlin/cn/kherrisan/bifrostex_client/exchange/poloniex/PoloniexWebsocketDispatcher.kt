package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher

class PoloniexWebsocketDispatcher(service: PoloniexService) : WebsocketDispatcher(service) {
    override suspend fun dispatch(bytes: ByteArray) {
        throw NotImplementedError()
    }
}