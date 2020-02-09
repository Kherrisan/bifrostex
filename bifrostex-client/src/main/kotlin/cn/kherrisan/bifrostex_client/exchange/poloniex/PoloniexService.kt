package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
@Lazy
class PoloniexService : ExchangeService() {

    @Autowired
    @Lazy
    override lateinit var spotMarketService: PoloniexSpotMarketService

    override val spotTradingService: SpotTradingService
        get() = TODO("Init the spotTradingService")
    override val marginTradingService: MarginTradingService
        get() = TODO("Init the marginTradingService")
}