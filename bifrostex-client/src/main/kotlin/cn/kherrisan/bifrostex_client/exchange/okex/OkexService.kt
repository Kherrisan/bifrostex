package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
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