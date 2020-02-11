package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.d64ungzip
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.JsonParser
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

const val PING_PERIOD = 20_1000L

@Component
class OkexWebsocketDispatcher(
        val staticConfiguration: OkexStaticConfiguration,
        runtimeConfig: OkexRuntimeConfig
) : WebsocketDispatcher(runtimeConfig) {

    override val host: String = staticConfiguration.spotMarketWsHost
    override val name: ExchangeName = ExchangeName.OKEX
    private var receivedInPeriod = false

    fun CoroutineScope.resetPingTimer() {
        launch(vertx.dispatcher()) {
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

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
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
                triggerSubscribedEvent(ch)
            } else {
                triggerUnsubscribedEvent(ch)
            }
        } else {
            val table = obj["table"].asString
            obj["data"].asJsonArray
                    .map { it.asJsonObject["instrument_id"].asString }
                    .map { "$table:$it" }
                    .forEach {
                        // deliver the data
                        val sub = subMap[it] as Subscription
                        sub.resolver(this, obj, sub)
                    }
        }
        resetPingTimer()
    }
}