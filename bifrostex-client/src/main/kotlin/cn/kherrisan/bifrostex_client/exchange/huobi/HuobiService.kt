package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.enumeration.AccountTypeEnum
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component

class HuobiService : ExchangeService() {

    lateinit var accountIdMap: Map<AccountTypeEnum, String>

    @Autowired
    override lateinit var spotMarketService: HuobiSpotMarketService

    @Autowired
    @Lazy
    override lateinit var spotTradingService: HuobiSpotTradingService

    @Autowired
    @Lazy
    override lateinit var marginTradingService: HuobiMarginTradingService
}