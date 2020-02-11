package cn.kherrisan.bifrostex_client.core

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import kotlinx.coroutines.CoroutineScope

class DefaultWebsocketDispatcher : WebsocketDispatcher(ExchangeRuntimeConfig()) {
    override val host: String = ""
    override val name: ExchangeName = ExchangeName.HUOBI

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        throw NotImplementedError()
    }
}