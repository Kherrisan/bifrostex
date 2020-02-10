package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component

class PoloniexService : ExchangeService() {

    @Autowired

    override lateinit var spotMarketService: PoloniexSpotMarketService

    override lateinit var spotTradingService: SpotTradingService
    override lateinit var marginTradingService: MarginTradingService
}