package cn.kherrisan.bifrostex_engine.core

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_engine.SpringBootStarter
import cn.kherrisan.bifrostex_engine.exchange.huobi.Huobi
import cn.kherrisan.bifrostex_engine.telegram.TelegramMessager
import cn.kherrisan.bifrostex_engine.telegram.TelegramVerticle
import io.vertx.core.Vertx
import org.apache.logging.log4j.LogManager
import org.springframework.boot.runApplication

val EXCHANGE_MAP = mapOf(
        ExchangeName.HUOBI to Huobi::class.java
)

object BifrostexEngine {

    private val logger = LogManager.getLogger()
    val vertx: Vertx = Vertx.vertx()

    fun init() {
        println("  ____   _   __                   _              \n" +
                " |  _ \\ (_) / _|                 | |             \n" +
                " | |_) | _ | |_  _ __  ___   ___ | |_  ___ __  __\n" +
                " |  _ < | ||  _|| '__|/ _ \\ / __|| __|/ _ \\\\ \\/ /\n" +
                " | |_) || || |  | |  | (_) |\\__ \\| |_|  __/ >  < \n" +
                " |____/ |_||_|  |_|   \\___/ |___/ \\__|\\___|/_/\\_\\\n" +
                "                                                 \n" +
                "                                                 ")
        runApplication<SpringBootStarter>()
        TelegramMessager
        vertx.deployVerticle(SpringContainer[TelegramVerticle::class.java])
    }
}