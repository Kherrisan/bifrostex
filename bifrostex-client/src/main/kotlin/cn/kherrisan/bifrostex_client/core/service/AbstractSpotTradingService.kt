package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpotTradingService
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderTypeEnum
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.http.DefaultSignedHttpService
import cn.kherrisan.bifrostex_client.core.http.SignedHttpService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.entity.*
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

abstract class AbstractSpotTradingService(
        val staticConfig: ExchangeStaticConfiguration,
        val dataAdaptor: ServiceDataAdaptor,
        val authenticationService: AuthenticationService
) : SpotTradingService
        , SignedHttpService by DefaultSignedHttpService(authenticationService)
        , ServiceDataAdaptor by dataAdaptor {

    val logger = LogManager.getLogger()

    override suspend fun subscribeBalance(symbol: Symbol?): Subscription<SpotBalance> {
        throw NotImplementedError()
    }

    override suspend fun subscribeOrder(symbol: Symbol): Subscription<SpotOrder> {
        throw NotImplementedError()
    }

    override suspend fun transferToMargin(currency: Currency, amount: BigDecimal, symbol: Symbol): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun transferToFuture(currency: Currency, amount: BigDecimal, symbol: Symbol): TransactionResult {
        throw NotImplementedError()
    }

    override suspend fun transferToSpot(currency: Currency, amount: BigDecimal, symbol: Symbol): TransactionResult {
        throw NotImplementedError()
    }

    open suspend fun createOrder(symbol: Symbol, price: BigDecimal, amount: BigDecimal, side: OrderSideEnum, type: OrderTypeEnum): TransactionResult {
        throw NotImplementedError()
    }

    open fun checkResponse(resp: HttpResponse<Buffer>): JsonElement {
        val t = resp.body().toString(StandardCharsets.UTF_8)
        return JsonParser.parseString(t)
    }

    fun jsonObject(resp: HttpResponse<Buffer>): JsonObject {
        val e = checkResponse(resp)
        return e.asJsonObject
    }

    fun jsonArray(resp: HttpResponse<Buffer>): JsonArray {
        val e = checkResponse(resp)
        return e.asJsonArray
    }

    open fun authUrl(path: String): String {
        if (path.startsWith("http")) {
            return path
        }
        return "${staticConfig.spotTradingHttpHost}$path"
    }
}