package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import org.junit.BeforeClass


abstract class TestSpotTradingMethod : TestExchangeMethod() {

    protected lateinit var spotTrading: SpotTradingService

    @BeforeClass
    override fun init() {
        super.init()
        spotTrading = exchangeService.spotTradingService
    }
}