package cn.kherrisan.bifrostex_client.exchange.okex

import cn.kherrisan.bifrostex_client.core.common.*
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import com.google.gson.GsonBuilder
import java.util.*

class OkexAuthenticateService(val host: String) : AuthenticationService {

    override fun signedHttpRequest(method: String, path: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>) {
        val apiKey = SpringContainer[OkexService::class.java].runtimeConfig.apiKey
        val apiSecret = SpringContainer[OkexService::class.java].runtimeConfig.secretKey
        val password = SpringContainer[OkexService::class.java].runtimeConfig.password
        val subPath = path.removePrefix(host)
        val ts = iso8601WithMS()
        var payload = "${ts}${method}$subPath"
        if (method.toUpperCase() == GET && params != null && params.isNotEmpty())
            payload = "$payload?${urlEncode(params)}"
        if (method.toUpperCase() == POST && params != null && params.isNotEmpty())
            payload = "${payload}${GsonBuilder().create().toJson(params)}"
        headers["OK-ACCESS-SIGN"] = Base64.getEncoder().encodeToString(hmacSHA256Signature(payload, apiSecret!!))
        headers["OK-ACCESS-KEY"] = apiKey!!
        headers["OK-ACCESS-TIMESTAMP"] = ts.toString()
        headers["OK-ACCESS-PASSPHRASE"] = password!!
    }
}