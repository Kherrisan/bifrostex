package cn.kherrisan.bifrostex_client.exchange.gateio

class GateioSingleChannelDispatcher(staticConfiguration: GateioStaticConfiguration, val ch: String, runtimeConfig: GateioRuntimeConfig) :
        GateioSpotMarketWebsocketDispatcher(staticConfiguration, runtimeConfig) {
}