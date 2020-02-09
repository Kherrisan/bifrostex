package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.common.ExchangeStaticConfiguration
import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.SpringContainer
import cn.kherrisan.bifrostex_client.core.http.HttpService
import cn.kherrisan.bifrostex_client.core.http.VertxHttpService

abstract class AbstractFutureMarketService(
        val staticConfig: ExchangeStaticConfiguration,
        val dataAdaptor: ServiceDataAdaptor
) : FutureMarketService
        , HttpService by SpringContainer[VertxHttpService::class.java]
        , ServiceDataAdaptor by dataAdaptor {

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