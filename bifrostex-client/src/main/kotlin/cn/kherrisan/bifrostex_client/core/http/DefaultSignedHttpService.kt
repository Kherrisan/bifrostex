package cn.kherrisan.bifrostex_client.core.http

import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.GET
import cn.kherrisan.bifrostex_client.core.common.HttpUtils
import cn.kherrisan.bifrostex_client.core.common.POST
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse

open class DefaultSignedHttpService(service: ExchangeService) :
        HttpUtils(), SignedHttpService, HttpService {

    private val http = VertxHttpService(service.vertx)
    val auth by lazy { service.buildAuthenticationService() }

    override fun auth(): AuthenticationService {
        return auth
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

    override suspend fun signedGet(url: String, params: MutableMap<String, String>, headers: MutableMap<String, String>): HttpResponse<Buffer> {
        auth.signedHttpRequest(GET, url, params as MutableMap<String, Any>, headers)
        return http.get(url, params, headers)
    }

    override suspend fun signedPost(url: String, httpMediaTypeEnum: HttpMediaTypeEnum, params: MutableMap<String, Any>, headers: MutableMap<String, String>): HttpResponse<Buffer> {
        auth.signedHttpRequest(POST, url, params, headers)
        return http.post(url, httpMediaTypeEnum, params, headers)
    }

    override suspend fun signedDelete(url: String, httpMediaTypeEnum: HttpMediaTypeEnum, params: MutableMap<String, Any>, headers: MutableMap<String, String>): HttpResponse<Buffer> {
        auth.signedHttpRequest("DELETE", url, params, headers)
        return http.delete(url, httpMediaTypeEnum, params, headers)
    }

    override suspend fun signedJsonPost(url: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>): HttpResponse<Buffer> {
        return signedPost(url, HttpMediaTypeEnum.JSON, params, headers)
    }

    override suspend fun signedUrlencodedPost(url: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>): HttpResponse<Buffer> {
        return signedPost(url, HttpMediaTypeEnum.URLENCODED, params, headers)
    }

    override suspend fun signedUrlencodedDelete(url: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>): HttpResponse<Buffer> {
        return signedDelete(url, HttpMediaTypeEnum.URLENCODED, params, headers)
    }
}