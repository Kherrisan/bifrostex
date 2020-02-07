package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.iid
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.vertx.kotlin.coroutines.awaitEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class KucoinWebsocketDispatcher(service: KucoinService) : WebsocketDispatcher(service) {

    val idMap = HashMap<Int, String>()

    private suspend fun resetPingTimer() {
        GlobalScope.launch {
            awaitEvent<Long> { vertx.setTimer(service.rtConfig.pingInterval!!.toLong(), it) }
            send(Gson().toJson(mapOf("id" to iid(), "type" to "ping")))
        }
    }

    override suspend fun dispatch(bytes: ByteArray) {
        val clear = bytes.toString(StandardCharsets.UTF_8)
//        logger.debug(clear)
        val obj = JsonParser.parseString(clear).asJsonObject
        val type = obj["type"].asString
        when (type) {
            "pong" -> resetPingTimer()
            "ack" -> {
                val ch = idMap[obj["id"].asString.toInt()]!!
                if (subMap[ch]!!.isSubscribed) {
                    triggerUnsubscribed(ch)
                } else {
                    triggerSubscribed(ch)
                }
                idMap.remove(obj["id"].asString.toInt())
            }
            "message" -> {
                val ch = obj["topic"].asString
                val sub = subMap[ch]!!
                sub.resolver(obj, sub)
            }
        }
    }
}