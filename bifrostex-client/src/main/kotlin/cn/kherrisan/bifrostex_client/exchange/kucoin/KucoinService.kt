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

class KucoinService : ExchangeService() {

    @Autowired

    override lateinit var spotMarketService: KucoinSpotMarketService

    @Autowired

    override lateinit var  spotTradingService: KucoinSpotTradingService

    override lateinit var marginTradingService: MarginTradingService
}