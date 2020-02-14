package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.DefaultCoroutineScope
import io.vertx.core.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

abstract class AbstractSubscription<T : Any> : Subscription<T>, CoroutineScope by DefaultCoroutineScope() {

    protected lateinit var subscribePromise: Promise<Any>
    protected lateinit var unsubscribePromise: Promise<Any>
    protected lateinit var requestPromise: Promise<T>
    protected val subscriptionChannel: Channel<T> = Channel(1024)
    var isSubscribed = false
    var data: Any? = null

    fun triggerSubscribedEvent() {
        subscribePromise.complete()
    }

    fun triggerUnsubscribedEvent() {
        unsubscribePromise.complete()
    }

    suspend fun triggerRequestedEvent() {
        requestPromise.complete()
    }

    fun deliver(t: T) {
        if (!subscriptionChannel.offer(t)) {
            //dataChannel is full
            subscriptionChannel.poll()
            subscriptionChannel.offer(t)
        }
    }
}