package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.ExchangeService

class DefaultWebsocketDispatcher(service: ExchangeService) :
        WebsocketDispatcher(service) {
    override suspend fun dispatch(bytes: ByteArray) {
        throw NotImplementedError()
    }
}