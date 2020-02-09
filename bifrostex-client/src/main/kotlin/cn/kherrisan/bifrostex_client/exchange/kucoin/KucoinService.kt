package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.*
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
@Lazy
class KucoinService : ExchangeService() {

    @Autowired
    @Lazy
    override lateinit var spotMarketService: KucoinSpotMarketService

    @Autowired
    @Lazy
    override lateinit var  spotTradingService: KucoinSpotTradingService

    override val marginTradingService: MarginTradingService
        get() = TODO("Init the marginTradingService")
}