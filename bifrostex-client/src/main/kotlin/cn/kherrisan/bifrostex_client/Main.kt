package cn.kherrisan.bifrostex_client

fun main(args: Array<String>) {
    val vertx = io.vertx.core.Vertx.vertx()
    vertx.setTimer(4000) {
        println("Hello World")
    }
}