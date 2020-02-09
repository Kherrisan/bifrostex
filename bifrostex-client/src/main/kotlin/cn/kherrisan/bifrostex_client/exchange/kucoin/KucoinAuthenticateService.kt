package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.common.hmacSHA256Signature
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import com.google.gson.Gson
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KucoinAuthenticateService @Autowired constructor(val service: KucoinService) : AuthenticationService {

    @Autowired
    private lateinit var staticConfiguration: KucoinStaticConfiguration

    override fun signedHttpRequest(method: String, path: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>) {
        headers["KC-API-KEY"] = service.runtimeConfig.apiKey!!
        headers["KC-API-TIMESTAMP"] = System.currentTimeMillis().toString()
        headers["KC-API-PASSPHRASE"] = service.runtimeConfig.password!!
        var payload = "${headers["KC-API-TIMESTAMP"]}${method.toUpperCase()}${path.removePrefix(staticConfiguration.spotMarketHttpHost)}"
        if (params.isNotEmpty()) {
            payload = "${payload}${Gson().toJson(params)}"
        }
        val sha = hmacSHA256Signature(payload, service.runtimeConfig.secretKey!!)
        headers["KC-API-SIGN"] = Base64.encodeBase64String(sha)
    }
}