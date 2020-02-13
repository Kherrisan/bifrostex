package cn.kherrisan.bifrostex_client.core.websocket

import com.google.gson.JsonElement
import io.vertx.core.Future
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.apache.commons.collections.buffer.CircularFifoBuffer
import org.apache.logging.log4j.LogManager

open class ResolvableSubscription<T : Any>(
        val channel: String,
        val dispatcher: WebsocketDispatcher,
        var resolver: suspend CoroutineScope.(JsonElement, ResolvableSubscription<T>) -> Unit)
    : AbstractSubscription<T>() {

    lateinit var requestPacket: () -> String
    lateinit var subPacket: () -> String
    lateinit var unsubPacket: () -> String

    val attachedSubscription: MutableList<AbstractSubscription<Any>> = mutableListOf()
    val buffer = CircularFifoBuffer(128)
    private val logger = LogManager.getLogger(this::class.java)

    override suspend fun unsubscribe() {
        logger.debug("Start to unsubscribe $channel")
        for (attach in attachedSubscription) {
            attach.unsubscribe()
        }
        // register the unsubscribe response promise
        val future = Future.future<Any> { unsubscribePromise = it }
        // send the unsubscribe packet
        dispatcher.send(unsubPacket())
        future.await()
        isSubscribed = false
        logger.debug("Unsubscribed to $channel")
        buffer.clear()
    }

    override suspend fun request(): ResolvableSubscription<T> {
        logger.debug("Start to request $channel")
        // register the unsubscribe response promise
        val future = Future.future<T> { requestPromise = it }
        // send the unsubscribe packet
        dispatcher.send(requestPacket())
        // waiting 1000ms for the upcoming unsub response packet
        delay(1000L)
        future.await()
        logger.debug("Get response")
        return this
    }

    override suspend fun subscribe(): ResolvableSubscription<T> {
        logger.debug("Start to subscribe $channel")
        buffer.clear()
        @Suppress("UNCHECKED_CAST")
        dispatcher.register(channel, this as AbstractSubscription<Any>)
        for (attach in attachedSubscription) {
            attach.subscribe()
        }
        // register the unsubscribe response promise
        val future = Future.future<Any> { subscribePromise = it }
        // send the unsubscribe packet
        dispatcher.send(subPacket())
        // waiting for the upcoming unsub response packet
        future.await()
        isSubscribed = true
        logger.debug("Subscribed to $channel")
        return this
    }

    override suspend fun receive(): T = subscriptionChannel.receive()

    fun attach(subscription: ResolvableSubscription<Any>) {
        attachedSubscription.add(subscription)
    }

    override fun isEmpty(): Boolean = subscriptionChannel.isEmpty
}