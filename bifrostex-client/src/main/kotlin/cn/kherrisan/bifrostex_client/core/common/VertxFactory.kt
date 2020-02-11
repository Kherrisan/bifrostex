package cn.kherrisan.bifrostex_client.core.common

import io.vertx.core.Vertx
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

object VertxContainer {
    private val instance: Vertx = Vertx.vertx()

    fun vertx(): Vertx = instance
}

/**
 * Vertx 工厂类
 */
@Component
class VertxFactory {

    /**
     * vertx 的工厂方法
     *
     * 全局使用这一个 vertx 对象
     * @return Vertx
     */
    @Bean
    fun vertx(): Vertx = VertxContainer.vertx()
}