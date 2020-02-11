package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import org.apache.logging.log4j.LogManager

abstract class TestExchangeMethod {
    protected val logger = LogManager.getLogger()
    abstract val exchangeName: ExchangeName
    protected lateinit var exchangeService: ExchangeService

    open fun init() {
        exchangeService = ExchangeFactory.build(exchangeName)
    }
}