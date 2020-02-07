package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.d64ungzip
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.JsonParser
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val PING_PERIOD = 20_1000L

class OkexWebsocketDispatcher(service: OkexService) : WebsocketDispatcher(service) {

    private var receivedInPeriod = false

    fun resetPingTimer() {
        GlobalScope.launch(vertx.dispatcher()) {
            receivedInPeriod = false
            delay(PING_PERIOD)
            if (!receivedInPeriod) {
                send("ping")
                delay(PING_PERIOD)
                if (!receivedInPeriod) {
                    reconnect()
                }
            }
        }
    }

    override suspend fun dispatch(bytes: ByteArray) {
        receivedInPeriod = true
        val clear = d64ungzip(bytes)
        if (clear == "pong") {
            return
        }
        val obj = JsonParser.parseString(clear).asJsonObject
        if (obj.has("event")) {
            // subscribe event
            val evt = obj["event"].asString
            val ch = obj["channel"].asString
            if (evt == "subscribe") {
                triggerSubscribed(ch)
            } else {
                triggerUnsubscribed(ch)
            }
        } else {
            val table = obj["table"].asString
            obj["data"].asJsonArray
                    .map { it.asJsonObject["instrument_id"].asString }
                    .map { "$table:$it" }
                    .forEach {
                        // deliver the data
                        val sub = subMap[it] as Subscription
                        sub.resolver(obj, sub)
                    }
        }
        resetPingTimer()
    }
}