package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.hmacSHA256Signature
import cn.kherrisan.bifrostex_client.core.common.urlEncode
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import org.apache.commons.codec.binary.Hex

class BinanceAuthenticateService(val service: BinanceService) : AuthenticationService {

    override fun signedHttpRequest(method: String, path: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>) {
        headers["X-MBX-APIKEY"] = service.rtConfig.apiKey!!
        params["recvWindow"] = "60000"
        params["timestamp"] = System.currentTimeMillis().toString()
        val payload = urlEncode(params)
        val sigBytes = hmacSHA256Signature(payload, service.rtConfig.secretKey!!)
        params["signature"] = Hex.encodeHexString(sigBytes)
    }
}