package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import cn.kherrisan.bifrostex_client.core.common.HttpUtils
import cn.kherrisan.bifrostex_client.core.common.IHttpUtils
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.http.HttpMediaTypeEnum
import cn.kherrisan.bifrostex_client.core.http.HttpService
import cn.kherrisan.bifrostex_client.core.http.VertxHttpService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import kotlinx.coroutines.CoroutineScope
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import java.nio.charset.StandardCharsets

abstract class AbstractSpotMarketService(val staticConfig: ExchangeStaticConfiguration,
                                         val dataAdaptor: ServiceDataAdaptor)
    : SpotMarketService
        , HttpService
        , ServiceDataAdaptor by dataAdaptor
        , IHttpUtils by HttpUtils() {

    /**
     * Websocket 消息发送和分发器，子类覆盖时必须 Autowired
     */
    open val dispatcher: WebsocketDispatcher = throw NotImplementedError()

    @Autowired
    val http: VertxHttpService

    val logger = LogManager.getLogger()

    public fun publicUrl(subPath: String): String {
        return "${staticConfig.spotMarketHttpHost}${subPath}"
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

    open fun <T : Any> newSubscription(channel: String, resolver: suspend CoroutineScope.(JsonElement, Subscription<T>) -> Unit): Subscription<T> {
        return newSubscription(channel, dispatcher, resolver)
    }

    abstract fun <T : Any> newSubscription(channel: String, dispatcher: WebsocketDispatcher, resolver: suspend CoroutineScope.(JsonElement, Subscription<T>) -> Unit): Subscription<T>
}