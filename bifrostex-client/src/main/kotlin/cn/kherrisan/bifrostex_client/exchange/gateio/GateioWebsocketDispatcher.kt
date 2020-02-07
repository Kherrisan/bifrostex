package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import cn.kherrisan.bifrostex_client.exchange.binance.OKEX_EMPTY_TRADE
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.nio.charset.StandardCharsets

val GATEIO_EMPTY_TRADE = OKEX_EMPTY_TRADE

open class GateioWebsocketDispatcher(service: GateioService) : WebsocketDispatcher(service) {

    val idMap = HashMap<Int, String>()

    override suspend fun handleCommandResponse(elem: JsonElement) {
        val obj = elem.asJsonObject
        val ch = idMap[obj["id"].asInt]!!
        idMap.remove(obj["id"].asInt)
        if (!subMap[ch]!!.isSubscribed) {
            triggerSubscribed(ch)
        } else {
            triggerUnsubscribed(ch)
        }
    }

    override suspend fun dispatch(bytes: ByteArray) {
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
            val sub = subMap[ch] as Subscription
            sub.resolver(obj, sub)
        }
    }
}