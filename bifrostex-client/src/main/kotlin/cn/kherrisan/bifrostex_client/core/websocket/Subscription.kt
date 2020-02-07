package cn.kherrisan.bifrostex_client.core.websocket

import com.google.gson.JsonElement
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import org.apache.commons.collections.buffer.CircularFifoBuffer

class Subscription<T : Any>(val channel: String, val dispatcher: WebsocketDispatcher, var resolver: suspend (JsonElement, Subscription<T>) -> Unit) {

    private lateinit var subscribePromise: Promise<Any>
    private lateinit var unsubscribePromise: Promise<Any>
    private lateinit var requestPromise: Promise<Any>
    var isSubscribed = false
    var dataChannel: Channel<T>? = Channel(Channel.UNLIMITED)
    lateinit var requestPacket: () -> String
    lateinit var subPacket: () -> String
    lateinit var unsubPacket: () -> String
    val attachedSubscription: MutableList<Subscription<Any>> = mutableListOf()
    val buffer = CircularFifoBuffer(128)
    var data: Any? = null
    private val logger = org.apache.logging.log4j.LogManager.getLogger(this::class.java)

    suspend fun unsubscribe(): Subscription<T> {
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
        logger.debug("Unsubscribe to $channel")
        buffer.clear()
        if (!dataChannel!!.isClosedForReceive) {
            dataChannel!!.close(ClosedReceiveChannelException("Active close."))
        }
        return this
    }

    suspend fun request(): Subscription<T> {
        logger.debug("Start to request $channel")
        // register the unsubscribe response promise
        val future = Future.future<Any> { requestPromise = it }
        // send the unsubscribe packet
        dispatcher.send(requestPacket())
        // waiting 3000ms for the upcoming unsub response packet
        delay(1000L)
        future.await()
        logger.debug("Requested to $channel")
        return this
    }

    suspend fun subscribe(): Subscription<T> {
        logger.debug("Start to subscribe $channel")
        buffer.clear()
        dispatcher.register(channel, this as Subscription<Any>)
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
        logger.debug("Subscribe to $channel")
        return this
    }

    fun triggerSubscribed() {
        subscribePromise.complete()
    }

    fun triggerUnsubscribed() {
        unsubscribePromise.complete()
    }

    fun triggerRequested() {
        requestPromise.complete()
    }

    suspend fun receive(): T = dataChannel!!.receive()

    fun deliver(t: T) {
        dataChannel!!.offer(t)
    }

    fun attach(subscription: Subscription<Any>) {
        attachedSubscription.add(subscription)
    }
}