package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ungzip
import cn.kherrisan.bifrostex_client.core.websocket.ResolvableSubscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HuobiSpotTradingWebsocketDispatcher @Autowired constructor(
        staticConfiguration: HuobiStaticConfiguration,
        runtimeConfig: HuobiRuntimeConfig
) : HuobiWebsocketDispatcher(staticConfiguration, runtimeConfig) {

    var isAuth = false
    override val host: String = staticConfiguration.spotTradingWsHost

    override fun newDispatcher(): WebsocketDispatcher {
        val d = HuobiSpotTradingWebsocketDispatcher(staticConfig, runtimeConfig as HuobiRuntimeConfig)
        childDispatcher.add(d)
        return d
    }

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        val clear = ungzip(bytes)
        logger.debug(clear)
        val obj = JsonParser.parseString(clear).asJsonObject
        val op = obj["op"].asString
        when (op) {
            "ping" -> {
                //ping-pong
                obj.remove("op")
                obj.addProperty("op", "pong")
                send(Gson().toJson(obj))
            }
            "auth" -> {
                //鉴权的响应
                //鉴权成功的响应格式：{"op":"auth","ts":1581497756963,"err-code":0,"data":{"user-id":5691027}}
                logger.debug(obj)
                val ch = obj["op"].asString
                //ch:"auth"
                val sub = subMap[ch] as ResolvableSubscription
                sub.resolver(this, obj, sub)
                triggerRequestedEvent(ch)
            }
            "sub" -> {
                //订阅的响应
                triggerSubscribedEvent(obj["topic"].asString)
            }
            "unsub" -> {
                //取消订阅的响应
                triggerUnsubscribedEvent(obj["topic"].asString)
            }
            "notify" -> {
                //推送的数据
                val ch = obj["topic"].asString
                val sub = subMap[ch] as ResolvableSubscription
                sub.resolver(this, obj, sub)
            }
        }
    }
}