package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class OkexService : ExchangeService() {

    @Autowired
    override lateinit var metaInfo: OkexMetaInfo

    @Autowired
    @Lazy
    override lateinit var spotMarketService: OkexSpotMarketService

    @Autowired
    @Lazy
    override lateinit var spotTradingService: OkexSpotTradingService

    @Autowired
    @Lazy
    override lateinit var marginTradingService: OkexMarginTradingService
}