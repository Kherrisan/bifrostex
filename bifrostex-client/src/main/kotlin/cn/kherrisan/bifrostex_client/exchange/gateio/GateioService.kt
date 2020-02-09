package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
@Lazy
class GateioService : ExchangeService() {

    @Autowired
    @Lazy
    override lateinit var spotMarketService: GateioSpotMarketService

    override val spotTradingService: SpotTradingService
        get() = TODO("Init the spotTradingService")
    override val marginTradingService: MarginTradingService
        get() = TODO("Init the marginTradingService")
}