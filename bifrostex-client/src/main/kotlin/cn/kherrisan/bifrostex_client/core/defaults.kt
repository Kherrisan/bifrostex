package cn.kherrisan.bifrostex_client.core

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.websocket.AbstractWebsocketDispatcher

class DefaultWebsocketDispatcher : AbstractWebsocketDispatcher(ExchangeRuntimeConfig()) {
    override val host: String = ""
    override val name: ExchangeName = ExchangeName.HUOBI

    override suspend fun dispatch(bytes: ByteArray) {
        throw NotImplementedError()
    }
}