package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher

abstract class AbstractExchangeSubService {

    open var publicHttpHost: String = throw NotImplementedError()

    open var publicWsHost: String = throw NotImplementedError()

    open var authHttpHost: String = throw NotImplementedError()

    open var authWsHost: String = throw NotImplementedError()

    open var dispatcher: WebsocketDispatcher = throw NotImplementedError()

    open fun publicUrl(subPath: String): String {
        if (subPath.startsWith("http")) {
            return subPath
        }
        return "${publicHttpHost}${subPath}"
    }

    open fun authUrl(subPath: String): String {
        if (subPath.startsWith("http")) {
            return subPath
        }
        return "${authHttpHost}${subPath}"
    }

}