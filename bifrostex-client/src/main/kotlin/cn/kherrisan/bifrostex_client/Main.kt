package cn.kherrisan.bifrostex_client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<SpringStarter>()
}

@SpringBootApplication
class SpringStarter