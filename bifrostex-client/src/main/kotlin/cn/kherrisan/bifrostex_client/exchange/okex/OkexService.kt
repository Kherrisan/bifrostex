package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OkexService : ExchangeService() {

    @Autowired
    override lateinit var spotMarketService: OkexSpotMarketService

    @Autowired
    override lateinit var  spotTradingService: OkexSpotTradingService

    @Autowired
    override lateinit var  marginTradingService: OkexMarginTradingService
}