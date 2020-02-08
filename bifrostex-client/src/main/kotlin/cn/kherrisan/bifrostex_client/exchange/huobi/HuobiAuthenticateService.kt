package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.gmt
import cn.kherrisan.bifrostex_client.core.common.hmacSHA256Signature
import cn.kherrisan.bifrostex_client.core.common.sortedUrlEncode
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import java.util.*

class HuobiAuthenticateService(val service: HuobiService) : AuthenticationService {
    override fun signedHttpRequest(method: String, path: String, params: MutableMap<String, Any>, headers: MutableMap<String, String>) {
        params["AccessKeyId"] = service.rtConfig.apiKey!!
        params["SignatureMethod"] = "HmacSHA256"
        params["SignatureVersion"] = "2"
        params["Timestamp"] = gmt()
        val sb = StringBuilder(1024)
        sb.append(method.toUpperCase()).append('\n')
                .append("api.huobi.pro").append('\n')
                .append(path.removePrefix(service.authHttpHost)).append('\n')
                .append(sortedUrlEncode(params))
        params["Signature"] = Base64.getEncoder().encodeToString(hmacSHA256Signature(sb.toString(), service.rtConfig.secretKey!!))
    }
}