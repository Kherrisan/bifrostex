package cn.kherrisan.bifrost_client

import io.vertx.core.http.HttpClientOptions

class TestVertxWebsocketClient {

//    @Test
    fun testConnectedToHuobi() {
        val options = HttpClientOptions()
        options.isSsl = true
        val client = io.vertx.core.Vertx.vertx().createHttpClient(options)
        client.webSocket(443, "api.huobi.pro", "/ws") {
            if (it.succeeded()) {
                println("Succeed")
            } else {
                println(it.cause().message)
                it.cause().printStackTrace()
            }
        }
        Thread.sleep(1000 * 30)
    }
}