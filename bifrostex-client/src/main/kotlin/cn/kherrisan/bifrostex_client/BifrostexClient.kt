package cn.kherrisan.bifrostex_client

import cn.kherrisan.bifrostex_client.core.common.SpringContainer
import org.springframework.boot.runApplication

object BifrostexClient {
    fun init() {
        runApplication<SpringStarter>()
    }
}