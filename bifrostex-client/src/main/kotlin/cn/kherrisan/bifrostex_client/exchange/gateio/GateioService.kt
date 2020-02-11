package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component

class GateioService : ExchangeService() {

    @Autowired
    override lateinit var spotMarketService: GateioSpotMarketService

    override lateinit var spotTradingService: SpotTradingService

    override lateinit var marginTradingService: MarginTradingService
}