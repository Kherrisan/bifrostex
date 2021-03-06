package cn.kherrisan.bifrostex_engine.core

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
object SpringContainer : ApplicationListener<ContextRefreshedEvent> {

    private lateinit var context: ApplicationContext

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        context = event.applicationContext
    }

    operator fun <T> get(cls: Class<T>): T = context.getBean(cls)
}