package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.service.SpotMarketService
import org.junit.jupiter.api.BeforeAll


abstract class TestSpotMarketMethod : TestExchangeMethod() {

    lateinit var spotMarketService: SpotMarketService

    @BeforeAll
    override fun init() {
        super.init()
        spotMarketService = exchangeService.spotMarketService
    }
}