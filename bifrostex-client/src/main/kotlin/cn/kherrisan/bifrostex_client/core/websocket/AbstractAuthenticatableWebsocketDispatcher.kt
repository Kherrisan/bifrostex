package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import com.google.gson.JsonElement
import io.vertx.core.Promise

abstract class AbstractAuthenticatableWebsocketDispatcher(
        runtimeConfig: ExchangeRuntimeConfig,
        val authenticationService: AuthenticationService)
    : AbstractWebsocketDispatcher(runtimeConfig) {

    protected var isAuthenticated = false
    protected var authenticationPromise = Promise.promise<Any>()

    suspend fun <T : Any> newAuthenticatedSubscription(channel: String, resolver: suspend (JsonElement, DefaultSubscription<T>) -> Unit): Subscription<T> {
        if (!isAuthenticated) {
            authenticate()
        }
        return newSubscription(channel, resolver)
    }

    fun triggerAuthenticationEvent() {
        authenticationPromise.complete()
        logger.debug("Authenticated")
    }

    abstract suspend fun authenticate()
}