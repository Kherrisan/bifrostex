package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.AbstractServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import org.junit.jupiter.api.BeforeAll


abstract class TestMarginTradingMethod : TestExchangeMethod() {

    lateinit var marginTradingService: MarginTradingService
    lateinit var metaInfo: ExchangeMetaInfo

    @BeforeAll
    override fun init() {
        super.init()
        marginTradingService = exchangeService.marginTradingService
        metaInfo = (marginTradingService as AbstractServiceDataAdaptor).metaInfo
    }
}