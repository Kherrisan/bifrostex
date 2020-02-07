package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.common.AbstractInitializer
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.http.DefaultSignedHttpService
import cn.kherrisan.bifrostex_client.core.http.SignedHttpService
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_client.entity.MarginInfo
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.TransactionResult
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

abstract class AbstractMarginTradingService(val service: ExchangeService)
    : AbstractInitializer(service)
        , MarginTradingService
        , SignedHttpService by DefaultSignedHttpService(service)
        , ServiceDataAdaptor by service.buildDataAdaptor() {

    lateinit var marginMetaInfo: Map<Symbol, MarginInfo>

    val spot: SpotMarginTradingService
        get() {
            return service.spotTradingService
        }

    override suspend fun allInitialize() {
        marginMetaInfo = getMarginInfo()
        initialize()
    }

    open fun authUrl(path: String): String {
        if (path.startsWith("http")) {
            return path
        }
        return "${service.authHttpHost}$path"
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
}
