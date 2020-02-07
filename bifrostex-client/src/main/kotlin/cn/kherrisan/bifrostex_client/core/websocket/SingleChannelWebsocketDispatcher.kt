package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.ExchangeService

abstract class SingleChannelWebsocketDispatcher(service: ExchangeService) :
        WebsocketDispatcher(service)