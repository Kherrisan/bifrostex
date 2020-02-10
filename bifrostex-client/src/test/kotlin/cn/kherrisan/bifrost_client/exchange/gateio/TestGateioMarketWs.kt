package cn.kherrisan.bifrost_client.exchange.gateio

import cn.kherrisan.bifrost_client.common.GROUP_GATEIO
import cn.kherrisan.bifrost_client.common.SUIT_SPOT_MARKET_WS_METHOD
import cn.kherrisan.bifrost_client.common.TestSubscribeMarketMethod
import cn.kherrisan.bifrostex_client.core.common.ExchangeName

class TestGateioMarketWs : TestSubscribeMarketMethod() {
    override val name: ExchangeName = ExchangeName.GATEIO
}