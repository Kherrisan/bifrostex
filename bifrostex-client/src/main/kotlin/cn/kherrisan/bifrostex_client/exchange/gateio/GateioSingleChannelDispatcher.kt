package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeName

class GateioSingleChannelDispatcher(staticConfiguration: GateioStaticConfiguration, val ch: String) :
        GateioWebsocketDispatcher(staticConfiguration){
}