package cn.kherrisan.bifrostex_engine.telegram

import cn.kherrisan.bifrostex_engine.BUS_TELEGRAM_MESSAGE
import cn.kherrisan.bifrostex_engine.core.BifrostexEngine

object TelegramMessager {

    private val vertx = BifrostexEngine.vertx

    fun send(text: String) {
        vertx.eventBus().send(BUS_TELEGRAM_MESSAGE, text)
    }
}