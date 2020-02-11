package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.enumeration.AccountTypeEnum
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class HuobiService : ExchangeService() {

    lateinit var accountIdMap: Map<AccountTypeEnum, String>

    @Autowired
    override lateinit var metaInfo: HuobiMetaInfo

    @Autowired
    override lateinit var runtimeConfig: HuobiRuntimeConfig

    @Autowired
    @Lazy
    override lateinit var spotMarketService: HuobiSpotMarketService

    @Autowired
    @Lazy
    override lateinit var spotTradingService: HuobiSpotTradingService

    @Autowired
    @Lazy
    override lateinit var marginTradingService: HuobiMarginTradingService
}