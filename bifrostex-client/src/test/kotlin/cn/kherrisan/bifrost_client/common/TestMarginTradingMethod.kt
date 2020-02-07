package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import org.testng.annotations.BeforeClass

abstract class TestMarginTradingMethod : TestExchangeMethod() {

    lateinit var marginTradingService: MarginTradingService

    @BeforeClass
    override fun init() {
        super.init()
        marginTradingService = exchangeService.marginTradingService
    }
}