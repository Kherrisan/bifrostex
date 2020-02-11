package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import org.junit.jupiter.api.BeforeAll


abstract class TestSpotTradingMethod : TestExchangeMethod() {

    protected lateinit var spotTrading: SpotTradingService

    @BeforeAll
    override fun init() {
        super.init()
        spotTrading = exchangeService.spotTradingService
    }
}