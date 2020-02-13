package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.websocket.DefaultSubscription
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.Trade
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.*

val OKEX_EMPTY_TRADE = Trade(Symbol.EMPTY, "", Date(), BigDecimal.ZERO, BigDecimal.ZERO, OrderSideEnum.NULL)

class BinanceSingleChannelDispatcher(staticConfig: BinanceStaticConfiguration, val ch: String, runtimeConfig: BinanceRuntimeConfig) :
        BinanceWebsocketDispatcher(staticConfig, runtimeConfig) {

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
            val sub = defaultSubscriptionMap[ch] as DefaultSubscription
            sub.resolver(this, obj, sub)
        }
    }
}