package cn.kherrisan.bifrostex_engine.telegram

import cn.kherrisan.bifrostex_engine.BUS_TELEGRAM_MESSAGE
import cn.kherrisan.bifrostex_engine.core.SpringContainer
import io.vertx.core.AbstractVerticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi

@Component
class TelegramVerticle(
        @Autowired
        private val springContainer: SpringContainer
) : AbstractVerticle() {

    override fun start() {
        val telegramMessager = springContainer[TelegramBot::class.java]
        vertx.eventBus().localConsumer<String>(BUS_TELEGRAM_MESSAGE) { msg ->
            vertx.executeBlocking<Any>({
                telegramMessager.sendMessage(msg.body())
            }, {})
        }
        ApiContextInitializer.init()
        val api = TelegramBotsApi()
        try {
            api.registerBot(telegramMessager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}