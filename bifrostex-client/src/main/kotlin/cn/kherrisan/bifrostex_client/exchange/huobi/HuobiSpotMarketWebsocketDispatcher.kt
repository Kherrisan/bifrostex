package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.iid
import cn.kherrisan.bifrostex_client.core.common.ungzip
import cn.kherrisan.bifrostex_client.core.websocket.AbstractWebsocketDispatcher
import cn.kherrisan.bifrostex_client.core.websocket.DefaultSubscription
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HuobiSpotMarketWebsocketDispatcher @Autowired constructor(
        val staticConfig: HuobiStaticConfiguration,
        runtimeConfig: HuobiRuntimeConfig
) : AbstractWebsocketDispatcher(runtimeConfig) {

    override val host: String = staticConfig.spotMarketWsHost
    override val name: ExchangeName = ExchangeName.HUOBI

    override suspend fun dispatch(bytes: ByteArray) {
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
                val sub = defaultSubscriptionMap[ch] as DefaultSubscription
                triggerRequestedEvent(ch)
                sub.resolver(obj, sub)
            }
            else -> {
                // dispatch the data
                try {
                    val ch = obj["ch"].asString
                    val sub = defaultSubscriptionMap[ch] as DefaultSubscription
                    sub.resolver(obj, sub)
                } catch (e: Exception) {
                    logger.error(e.message)
                    logger.error("Error in dispatch: $clear")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun <T : Any> newSubscription(channel: String, resolver: suspend (JsonElement, DefaultSubscription<T>) -> Unit): DefaultSubscription<T> {
        val subscription = super.newSubscription(channel, resolver)
        subscription.requestPacket = { Gson().toJson(mapOf("req" to channel, "id" to iid())) }
        subscription.subPacket = { Gson().toJson(mapOf("sub" to channel, "id" to iid())) }
        subscription.unsubPacket = { Gson().toJson(mapOf("unsub" to channel, "id" to iid())) }
        return subscription
    }

    override fun newDispatcher(): AbstractWebsocketDispatcher {
        val d = HuobiSpotMarketWebsocketDispatcher(staticConfig, runtimeConfig as HuobiRuntimeConfig)
        childDispatcher.add(d)
        return d
    }
}