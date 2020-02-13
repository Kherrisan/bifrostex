package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.websocket.ResolvableSubscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import cn.kherrisan.bifrostex_client.exchange.binance.OKEX_EMPTY_TRADE
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

val GATEIO_EMPTY_TRADE = OKEX_EMPTY_TRADE

@Component
open class GateioWebsocketDispatcher @Autowired constructor(
        staticConfiguration: GateioStaticConfiguration,
        runtimeConfig: GateioRuntimeConfig
) : WebsocketDispatcher(runtimeConfig) {

    val idMap = HashMap<Int, String>()
    override val host: String = staticConfiguration.spotMarketWsHost
    override val name: ExchangeName = ExchangeName.GATEIO

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

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        val obj = JsonParser.parseString(bytes.toString(StandardCharsets.UTF_8)).asJsonObject
        if (obj.has("result")) {
            // sub and unsub event
            handleCommandResponse(obj)
        } else {
            val m = obj["method"].asString.removeSuffix(".update")
            val sym: String
            sym = when {
                m.contains("depth") -> {
                    // 我也不知道为什么gateio的response格式这么奇葩。。。
                    obj["params"].asJsonArray.last().asString
                }
                m.contains("kline") -> {
                    obj["params"].asJsonArray[0].asJsonArray.last().asString
                }
                else -> {
                    obj["params"].asJsonArray[0].asString
                }
            }
            val ch = "$m:$sym"
            val sub = subMap[ch] as ResolvableSubscription
            sub.resolver(this, obj, sub)
        }
    }
}