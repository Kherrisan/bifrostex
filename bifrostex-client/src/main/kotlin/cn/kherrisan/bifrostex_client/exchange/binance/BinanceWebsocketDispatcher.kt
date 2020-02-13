package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.websocket.ResolvableSubscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.vertx.core.Promise
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
open class BinanceWebsocketDispatcher @Autowired constructor(
        staticConfig: BinanceStaticConfiguration,
        runtimeConfig: BinanceRuntimeConfig
) : WebsocketDispatcher(runtimeConfig) {

    override val host: String = staticConfig.spotMarketWsHost
    override val name: ExchangeName = ExchangeName.BINANCE

    val idMap = HashMap<Int, String>()
    var pongTimer: Long? = null
    var pongPromise: Promise<Any>? = null

    override suspend fun handleCommandResponse(elem: JsonElement) {
        val obj = elem.asJsonObject
        val ch = idMap[obj["id"].asInt]!!
        idMap.remove(obj["id"].asInt)
        if (!subMap[ch]!!.isSubscribed) {
            triggerSubscribedEvent(ch)
        } else {
            triggerUnsubscribedEvent(ch)
        }
    }

    override suspend fun handlePing(bytes: ByteArray): Boolean {
        val clear = bytes.toString(StandardCharsets.UTF_8)
        try {
            //receive ping frame(a timestamp)
            logger.debug("Receive ping: ${clear.toLong()}")
            if (pongPromise == null || pongPromise!!.future().isComplete) {
                //只有在第一次 pong 或者上一个 pong 已经完成之后才会发起一个新的延时 pong
                //Binance 每次收到 pong 之后会立刻回复 ping，这里如果直接回复 pong 的话就会导致大量的 ping-pong
                //因此这里延时 5min 回复 pong
                pongPromise = Promise.promise<Any>()
                pongTimer = vertx.setTimer(300_000) {
                    sendPong(System.currentTimeMillis().toString())
                    pongPromise!!.complete()
                }
                logger.debug("Scheduled pong in 5 minutes.")
            }
            return true
        } catch (e: Exception) {

        }
        return false
    }

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        if (handlePing(bytes)) {
            return
        }
        val clear = bytes.toString(StandardCharsets.UTF_8)
        val obj = JsonParser.parseString(clear).asJsonObject
        if (obj.has("result")) {
            // subscription or unsubscription event
            handleCommandResponse(obj)
        } else {
            try {
                var e = obj["e"].asString
                val s = obj["s"].asString
                if (e.startsWith("24hr")) {
                    e = e.removePrefix("24hr").decapitalize()
                }
                if (e.contains("kline")) {
                    e = "${e}_${obj["k"].asJsonObject["i"].asString}"
                }
                val ch = "$s@$e".toLowerCase()
                val sub = subMap[ch] as ResolvableSubscription
                sub.resolver(this, obj, sub)
            } catch (exception: Exception) {
                logger.error(exception)
                logger.error(obj)
            }
        }
    }

    suspend fun sendSubscriptionMessage(ch: String) {
    }

    override suspend fun close() {
        if (pongPromise != null && !pongPromise!!.future().isComplete) {
            pongPromise!!.fail("Active close.")
        }
        super.close()
    }
}