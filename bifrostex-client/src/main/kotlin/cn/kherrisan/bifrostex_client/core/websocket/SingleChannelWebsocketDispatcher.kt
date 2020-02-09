package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.ExchangeService

abstract class SingleChannelWebsocketDispatcher(val channel: String) :
        WebsocketDispatcher()