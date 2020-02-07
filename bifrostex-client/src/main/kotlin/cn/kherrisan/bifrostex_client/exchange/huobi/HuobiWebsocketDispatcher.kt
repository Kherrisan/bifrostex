package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ungzip
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.Gson
import com.google.gson.JsonParser

class HuobiWebsocketDispatcher(service: HuobiService) :
        WebsocketDispatcher(service) {

    override suspend fun dispatch(bytes: ByteArray) {
        val clear = ungzip(bytes)
//        logger.debug(clear)
        val obj = JsonParser.parseString(clear).asJsonObject
        when {
            obj.has("ping") -> {
                // ping-pong
                send(Gson().toJson(mapOf("pong" to obj["ping"].asLong)))
            }
            obj.has("subbed") -> {
                // sub-event
                triggerSubscribed(obj["subbed"].asString)
            }
            obj.has("unsubbed") -> {
                // unsub-event
                triggerUnsubscribed(obj["unsubbed"].asString)
            }
            obj.has("rep") -> {
                val ch = obj["rep"].asString
                val sub = subMap[ch] as Subscription
                sub.resolver(obj, sub)
            }
            else -> {
                // dispatch the data
                try {
                    val ch = obj["ch"].asString
                    val sub = subMap[ch] as Subscription
                    sub.resolver(obj, sub)
                } catch (e: Exception) {
                    logger.error(e.message)
                    logger.error("Error in dispatch: $clear")
                    e.printStackTrace()
                }
            }
        }
    }
}