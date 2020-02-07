package cn.kherrisan.bifrostex_engine.core

import cn.kherrisan.bifrostex_client.core.common.ExchangeFactory
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import javax.annotation.PostConstruct

abstract class AbstractExchange : Exchange {

    override lateinit var service: ExchangeService

    @PostConstruct
    fun initService() {
        service = ExchangeFactory.build(name, BifrostexEngine.vertx, config)
    }
}