package cn.kherrisan.bifrostex_client.exchange.gateio

class GateioSingleChannelDispatcher(service: GateioService, val ch: String) :
        GateioWebsocketDispatcher(service)