package cn.kherrisan.bifrostex_client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

object BifrostexClient {
    fun init() {
        runApplication<SpringStarter>()
    }
}

@SpringBootApplication
class SpringStarter