package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig

abstract class SingleChannelWebsocketDispatcher(val channel: String, runtimeConfig: ExchangeRuntimeConfig) :
        AbstractWebsocketDispatcher(runtimeConfig)