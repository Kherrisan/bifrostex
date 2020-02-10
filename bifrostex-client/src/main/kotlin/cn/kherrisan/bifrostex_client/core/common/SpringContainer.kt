package cn.kherrisan.bifrostex_client.core.common

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

object SpringContainer : ApplicationContextAware {

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    private lateinit var context: ApplicationContext

    operator fun <T> get(cls: Class<T>): T = context.getBean(cls)
}

@Component
@Lazy(false)
class SpringContainerFactory {

    @Bean
    @Lazy(false)
    fun springContainer(): SpringContainer = SpringContainer
}