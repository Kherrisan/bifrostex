package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.DefaultScope
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_client.core.common.objSimpName
import com.google.gson.JsonElement
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.WebSocket
import io.vertx.core.net.ProxyOptions
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

abstract class WebsocketDispatcher(val service: ExchangeService) : CoroutineScope by DefaultScope(service.vertx) {

    protected val logger: Logger = LogManager.getLogger()
    protected val subMap: MutableMap<String, Subscription<Any>> = ConcurrentHashMap()
    private lateinit var ws: WebSocket
    var state: EndpointStateEnum = EndpointStateEnum.INIT
    var url: String = service.publicWsHost
        set(value) {
            field = value
        }
    val vertx
        get() = service.vertx
    var dispatcherLoop: Job? = null

    fun triggerSubscribed(ch: String) {
        logger.debug("Trigger subscribed to $ch")
        subMap[ch]!!.triggerSubscribed()
    }

    suspend fun triggerUnsubscribed(ch: String) {
        val sub = unregister(ch)
        logger.debug("Trigger unsubscribed to $ch")
        sub.triggerUnsubscribed()
        if (subMap.isEmpty()) {
            close()
        }
    }

    fun register(ch: String, subscription: Subscription<Any>) {
        logger.debug("Register $ch")
        subMap[ch] = subscription
    }

    fun unregister(ch: String): Subscription<Any> {
        logger.debug("Unregister $ch")
        return subMap.remove(ch)!!
    }

    private fun buildHttpClientOptions(): HttpClientOptions {
        val options = HttpClientOptions()
        val rt = service.rtConfig
        if (rt.proxyHost != null && rt.proxyPort != null) {
            val po = ProxyOptions()
            po.host = rt.proxyHost
            po.port = rt.proxyPort!!
            options.proxyOptions = po
        }
        return options
    }

    suspend fun resubscribeAll() {
        subMap.values.forEach { it.unsubscribe() }
        subMap.values.forEach { it.subscribe() }
    }

    suspend fun restart() {
        val subMapCopy = HashMap(subMap)
        logger.debug("Restarting...")
        reconnect()
        logger.debug("Start to subscribe ${subMapCopy.values.joinToString(", ")}")
        subMapCopy.values.forEach { it.subscribe() }
    }

    suspend fun reconnect() {
        close()
        logger.debug("Waiting 2s for connecting.")
        delay(2000)
        connect()
    }

    suspend fun connect() {
        logger.debug("Start to connect to $url")
        val hco = buildHttpClientOptions()
        val uri = URI.create(url)
        var port = uri.port
        if (uri.scheme == "wss") {
            hco.isSsl = true
        }
        if (uri.scheme == "wss" && uri.port == -1) {
            port = 443
        }
        state = EndpointStateEnum.CONNECTING
        val rp = if (uri.query == null) {
            uri.path
        } else {
            "${uri.path}?${uri.query}"
        }
        ws = awaitResult { vertx.createHttpClient(hco).webSocket(port, uri.host, rp, it) }
        logger.debug("Connected to $url")
        val channel = Channel<Buffer>(Channel.UNLIMITED)
        ws.handler {
            channel.offer(it)
        }
        state = EndpointStateEnum.CONNECTED
        launch(vertx.dispatcher()) {
            while (true) {
                try {
                    val buffer = channel.receive()
                    dispatch(buffer.bytes)
                } catch (e: CancellationException) {
                    channel.close()
                    logger.debug("Websocket channel is closed.")
                }
            }
        }
    }

    /**
     * 关闭该 dispatcher，并回收相关的资源。
     *
     * 子类可以复写该方法，但是一定要记得要在最后调用父类方法。
     */
    open suspend fun close() {
        logger.debug("Start to close the ${objSimpName(this)}.")
        state = EndpointStateEnum.CLOSING
        dispatcherLoop?.cancel(CancellationException("Active cancel this WebsocketDispatcher."))
        awaitResult<Void> { ws.close(it) }
        logger.debug("${objSimpName(this)} closed.")
        state = EndpointStateEnum.CLOSED
        subMap.clear()
    }

    abstract suspend fun dispatch(bytes: ByteArray)

    /**
     * 发送 pong 帧
     *
     * pong 帧的格式符合 RFC6455 的要求，目前只有 Binance 用到了这个方法。
     *
     * @param text String
     */
    fun sendPong(text: String) {
        ws.writePing(Buffer.buffer(text))
        logger.debug("Websocket has sent pong frame: $text")
    }

    suspend fun send(text: String) {
        if (state == EndpointStateEnum.INIT || state == EndpointStateEnum.CLOSED) {
            connect()
        }
        while (state == EndpointStateEnum.CONNECTING || state == EndpointStateEnum.CLOSING) delay(100)
        ws.writeTextMessage(text)
        logger.debug("Websocket has sent $text")
    }

    open suspend fun handlePing(bytes: ByteArray): Boolean {
        return false
    }

    open suspend fun handleCommandResponse(elem: JsonElement) {
    }
}