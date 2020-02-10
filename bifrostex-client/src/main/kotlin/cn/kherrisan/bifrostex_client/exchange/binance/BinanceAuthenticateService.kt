package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.SpringContainer
import cn.kherrisan.bifrostex_client.core.common.hmacSHA256Signature
import cn.kherrisan.bifrostex_client.core.common.urlEncode
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import org.apache.commons.codec.binary.Hex
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class BinanceAuthenticateService : AuthenticationService {

    override fun signedHttpRequest(method: String, path: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>) {
        val apiKey = SpringContainer[BinanceService::class.java].runtimeConfig.apiKey
        val apiSecret = SpringContainer[BinanceService::class.java].runtimeConfig.secretKey
        headers["X-MBX-APIKEY"] = apiKey!!
        params["recvWindow"] = "60000"
        params["timestamp"] = System.currentTimeMillis().toString()
        val payload = urlEncode(params)
        val sigBytes = hmacSHA256Signature(payload, apiSecret!!)
        params["signature"] = Hex.encodeHexString(sigBytes)
    }
}