package cn.kherrisan.bifrostex_client

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    val vertx = Vertx.vertx()
    runBlocking {
        launch(vertx.dispatcher()) {
            println(coroutineContext)
            throw RuntimeException("dfadf")
            launch(vertx.dispatcher()) {
                println(coroutineContext)
            }
        }
        launch(vertx.dispatcher()) {
            delay(3000)
            println(coroutineContext)
        }
    }
}