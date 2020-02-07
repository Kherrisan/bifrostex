package cn.kherrisan.bifrostex_client.core.common

import io.vertx.core.Vertx

object ExchangeFactory {
    fun build(name: ExchangeName = ExchangeName.HUOBI, vertx: Vertx = Vertx.vertx(), config: RuntimeConfiguration = RuntimeConfiguration()): ExchangeService {
        val service = name.exchangeServiceClass.getConstructor(Vertx::class.java).newInstance(vertx)
        service.rtConfig = config
        service.name = name
        return service
    }
}