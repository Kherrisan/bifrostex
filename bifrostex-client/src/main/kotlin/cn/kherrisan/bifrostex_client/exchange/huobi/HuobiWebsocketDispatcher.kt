package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ungzip
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HuobiWebsocketDispatcher @Autowired constructor(
        val staticConfig: HuobiStaticConfiguration
) : WebsocketDispatcher() {

    override val host: String = staticConfig.spotMarketWsHost
    override val name: ExchangeName = ExchangeName.HUOBI

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        val clear = ungzip(bytes)
        logger.trace(clear)
        val obj = JsonParser.parseString(clear).asJsonObject
        when {
            obj.has("ping") -> {
                // ping-pong
                send(Gson().toJson(mapOf("pong" to obj["ping"].asLong)))
            }
            obj.has("subbed") -> {
                // sub-event
                triggerSubscribedEvent(obj["subbed"].asString)
            }
            obj.has("unsubbed") -> {
                // unsub-event
                triggerUnsubscribedEvent(obj["unsubbed"].asString)
            }
            obj.has("rep") -> {
                val ch = obj["rep"].asString
                val sub = subMap[ch] as Subscription
                sub.resolver(this, obj, sub)
            }
            else -> {
                // dispatch the data
                try {
                    val ch = obj["ch"].asString
                    val sub = subMap[ch] as Subscription
                    sub.resolver(this, obj, sub)
                } catch (e: Exception) {
                    logger.error(e.message)
                    logger.error("Error in dispatch: $clear")
                    e.printStackTrace()
                }
            }
        }
    }
}