package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.VertxContainer
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.collections.buffer.CircularFifoBuffer

class SynchronizedSubscription<T : Any>() : AbstractSubscription<T>() {

    private val vertx = VertxContainer.vertx()
    private val childSubscription = mutableListOf<AbstractSubscription<Any>>()
    private val bufferList = mutableListOf<CircularFifoBuffer>()
    private lateinit var resolver: (List<Any>, AbstractSubscription<T>) -> Unit

    suspend fun handleSynchronizeEvents() {
        val dataList = mutableListOf<Any>()
        for (buf in bufferList) {
            if (buf.size == 0) {
                return
            }
            dataList.add(buf.remove())
        }
        resolver(dataList, this)
    }

    fun addChild(sub: AbstractSubscription<Any>): SynchronizedSubscription<T> {
        val buffer = CircularFifoBuffer(2)
        bufferList.add(buffer)
        childSubscription.add(sub)
        launch(vertx.dispatcher()) {
            while (true) {
                try {
                    val data = sub.receive()
                    buffer.add(data)
                } catch (c: CancellationException) {
                    return@launch
                }
            }
        }
        return this
    }

    fun resolve(resolver: (List<Any>, AbstractSubscription<T>) -> Unit) {
        this.resolver = resolver
    }

    override suspend fun subscribe(): Subscription<T> {
        for (ch in childSubscription) {
            ch.subscribe()
        }
        launch(vertx.dispatcher()) {
            while (true) {
                try {
                    handleSynchronizeEvents()
                    delay(10)
                } catch (cancel: CancellationException) {
                    subscriptionChannel.close(cancel)
                    return@launch
                }
            }
        }
        return this
    }

    override suspend fun unsubscribe() {
        for (ch in childSubscription) {
            ch.unsubscribe()
        }
    }

    override suspend fun request(): Subscription<T> {
        throw NotImplementedError()
    }

    override suspend fun receive(): T = subscriptionChannel.receive()

    override fun isEmpty(): Boolean = subscriptionChannel.isEmpty
}