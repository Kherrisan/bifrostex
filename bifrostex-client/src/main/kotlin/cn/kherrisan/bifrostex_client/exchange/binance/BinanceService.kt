package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
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

class BinanceService : ExchangeService() {

    @Autowired

    override lateinit var spotMarketService: BinanceSpotMarketService

    @Autowired

    override lateinit var spotTradingService: BinanceSpotTradingService

    @Autowired

    override lateinit var marginTradingService: BinanceMarginTradingService
}