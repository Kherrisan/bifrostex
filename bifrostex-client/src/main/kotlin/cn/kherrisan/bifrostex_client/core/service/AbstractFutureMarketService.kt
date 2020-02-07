package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.common.AbstractInitializer
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.http.DefaultSignedHttpService
import cn.kherrisan.bifrostex_client.core.http.HttpService

abstract class AbstractFutureMarketService(service: ExchangeService)
    : AbstractInitializer(service)
        , FutureMarketService
        , HttpService by DefaultSignedHttpService(service)
        , ServiceDataAdaptor by service.buildDataAdaptor() {

    abstract val publicHttpHost: String
    open val authHttpHost = ""
    open val publicWsHost = ""

    fun publicHttpUrl(path: String): String {
        if (path.startsWith("http"))
            return path
        return "$publicHttpHost$path"
    }

    fun authHttpUrl(path: String): String {
        if (path.startsWith("http"))
            return path
        return "$authHttpHost$path"
    }
}