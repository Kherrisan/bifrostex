package cn.kherrisan.bifrostex_client.core.common

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

object RuntimeConfigContainer {

    private val map: MutableMap<ExchangeName, RuntimeConfiguration> = ConcurrentHashMap()

    operator fun get(exchangeName: ExchangeName): RuntimeConfiguration? = map[exchangeName]

    operator fun set(exchangeName: ExchangeName, c: RuntimeConfiguration) {
        map[exchangeName] = c
    }
}