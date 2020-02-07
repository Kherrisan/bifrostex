package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.common.*
import cn.kherrisan.bifrostex_client.core.http.HttpMediaTypeEnum
import cn.kherrisan.bifrostex_client.core.http.HttpService
import cn.kherrisan.bifrostex_client.core.http.VertxHttpService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import java.nio.charset.StandardCharsets

abstract class AbstractSpotMarketService(var service: ExchangeService) :
        AbstractInitializer(service)
        , SpotMarketService
        , HttpService
        , ServiceDataAdaptor by service.buildDataAdaptor()
        , IHttpUtils by HttpUtils() {

    open val dispatcher: WebsocketDispatcher
        get() = service.dispatcher

    private val http = VertxHttpService(service.vertx)

    override suspend fun allInitialize() {
        initialize()
    }

    override suspend fun get(path: String, params: Map<String, String>?, headers: Map<String, String>?): HttpResponse<Buffer> {
        return http.get(path, params, headers)
    }

    override suspend fun post(path: String, httpMediaTypeEnum: HttpMediaTypeEnum, params: Map<String, Any>?, headers: Map<String, String>?): HttpResponse<Buffer> {
        return http.post(path, httpMediaTypeEnum, params, headers)
    }

    override suspend fun delete(path: String, httpMediaTypeEnum: HttpMediaTypeEnum, params: Map<String, Any>?, headers: Map<String, String>?): HttpResponse<Buffer> {
        return http.delete(path, httpMediaTypeEnum, params, headers)
    }

    open fun publicUrl(path: String): String {
        if (path.startsWith("http")) {
            return path
        }
        return "${service.publicHttpHost}$path"
    }

    fun jsonObject(resp: HttpResponse<Buffer>): JsonObject {
        val e = checkResponse(resp)
        return e.asJsonObject
    }

    fun jsonArray(resp: HttpResponse<Buffer>): JsonArray {
        val e = checkResponse(resp)
        return e.asJsonArray
    }

    open fun checkResponse(resp: HttpResponse<Buffer>): JsonElement {
        val t = resp.body().toString(StandardCharsets.UTF_8)
        return JsonParser.parseString(t)
    }

    open fun <T : Any> newSubscription(channel: String, resolver: suspend (JsonElement, Subscription<T>) -> Unit): Subscription<T> {
        return newSubscription(channel, dispatcher, resolver)
    }

    abstract fun <T : Any> newSubscription(channel: String, dispatcher: WebsocketDispatcher, resolver: suspend (JsonElement, Subscription<T>) -> Unit): Subscription<T>
}