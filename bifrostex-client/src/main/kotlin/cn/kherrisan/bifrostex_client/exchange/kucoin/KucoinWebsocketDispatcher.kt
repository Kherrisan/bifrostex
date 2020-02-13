package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.iid
import cn.kherrisan.bifrostex_client.core.websocket.ResolvableSubscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.vertx.kotlin.coroutines.awaitEvent
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class KucoinWebsocketDispatcher @Autowired constructor(runtimeConfig: KucoinRuntimeConfig) : WebsocketDispatcher(runtimeConfig) {

    override val host: String = ""
    override val name: ExchangeName = ExchangeName.KUCOIN

    val idMap = HashMap<Int, String>()

    private suspend fun CoroutineScope.resetPingTimer() {
        launch(vertx.dispatcher()) {
            awaitEvent<Long> { vertx.setTimer(runtimeConfig.pingInterval!!.toLong(), it) }
            send(Gson().toJson(mapOf("id" to iid(), "type" to "ping")))
        }
    }

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        val clear = bytes.toString(StandardCharsets.UTF_8)
//        logger.debug(clear)
        val obj = JsonParser.parseString(clear).asJsonObject
        val type = obj["type"].asString
        when (type) {
            "pong" -> resetPingTimer()
            "ack" -> {
                val ch = idMap[obj["id"].asString.toInt()]!!
                if (subMap[ch]!!.isSubscribed) {
                    triggerUnsubscribedEvent(ch)
                } else {
                    triggerSubscribedEvent(ch)
                }
                idMap.remove(obj["id"].asString.toInt())
            }
            "message" -> {
                val ch = obj["topic"].asString
                val sub = subMap[ch]!! as ResolvableSubscription
                sub.resolver(this, obj, sub)
            }
        }
    }
}