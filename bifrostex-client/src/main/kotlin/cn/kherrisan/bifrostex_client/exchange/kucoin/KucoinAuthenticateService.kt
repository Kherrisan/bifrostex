package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.hmacSHA256Signature
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import com.google.gson.Gson
import org.apache.commons.codec.binary.Base64

class KucoinAuthenticateService(val service: KucoinService) : AuthenticationService {
    override fun signedHttpRequest(method: String, path: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>) {
        headers["KC-API-KEY"] = service.rtConfig.apiKey!!
        headers["KC-API-TIMESTAMP"] = System.currentTimeMillis().toString()
        headers["KC-API-PASSPHRASE"] = service.rtConfig.password!!
        var payload = "${headers["KC-API-TIMESTAMP"]}${method.toUpperCase()}${path.removePrefix(service.publicHttpHost)}"
        if (params.isNotEmpty()) {
            payload = "${payload}${Gson().toJson(params)}"
        }
        val sha = hmacSHA256Signature(payload, service.rtConfig.secretKey!!)
        headers["KC-API-SIGN"] = Base64.encodeBase64String(sha)
    }
}