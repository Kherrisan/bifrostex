package cn.kherrisan.bifrostex_client.core.websocket

import cn.kherrisan.bifrostex_client.core.common.*
import com.google.gson.JsonElement
import io.vertx.core.Vertx
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
import java.util.concurrent.atomic.AtomicReference

abstract class AbstractWebsocketDispatcher(val runtimeConfig: ExchangeRuntimeConfig)
    : WebsocketDispatcher, CoroutineScope by DefaultCoroutineScope() {

    /**
     * 注入全局的 vertx 单例对象
     */
    val vertx: Vertx = VertxContainer.vertx()

    abstract val host: String
    abstract val name: ExchangeName

    protected val logger: Logger = LogManager.getLogger()
    protected var defaultSubscriptionMap: MutableMap<String, AbstractSubscription<Any>> = ConcurrentHashMap()
    protected var synchronizeSubscriptionList: MutableList<SynchronizedSubscription<Any>> = mutableListOf()
    private lateinit var ws: WebSocket
    private var state: AtomicReference<EndpointStateEnum> = AtomicReference(EndpointStateEnum.CLOSED)
    private var eventLoop: Job? = null
    val childDispatcher: MutableList<AbstractWebsocketDispatcher> = mutableListOf()
    private val receiveChannel: Channel<Buffer> = Channel(1024)
    private val sendChannel: Channel<String> = Channel(1024)

    init {
        launchEventLoop()
    }

    protected fun launchEventLoop() {
        logger.debug("Start running dispatcher loop ${objSimpName(this)}")
        eventLoop = launch(vertx.dispatcher()) {
            while (true) {
                try {
                    handleChannelEvents()
                    delay(20)
                } catch (e: CancellationException) {
                    sendChannel.close(e)
                    receiveChannel.close(e)
                    logger.debug("Websocket channel is closed.")
                    return@launch
                }
            }
        }
    }

    override fun triggerSubscribedEvent(ch: String) {
        logger.debug("Trigger subscribed to $ch")
        defaultSubscriptionMap[ch]!!.triggerSubscribedEvent()
    }

    /**
     * 触发取消订阅事件
     *
     * 触发 subscription 的 triggerUnsubscribedEvent，来完成 promise。没有启动子协程，不需要 CoroutineScope
     * @param ch String
     */
    override suspend fun triggerUnsubscribedEvent(ch: String) {
        val sub = unregister(ch)
        logger.debug("Trigger unsubscribed to $ch")
        sub.triggerUnsubscribedEvent()
        if (defaultSubscriptionMap.isEmpty()) {
            for (sync in synchronizeSubscriptionList) {
                sync.close()
            }
            synchronizeSubscriptionList.clear()
            close()
        }
    }

    override suspend fun triggerRequestedEvent(ch: String) {
        logger.debug("Trigger requested to $ch")
        defaultSubscriptionMap[ch]!!.triggerRequestedEvent()
    }

    fun register(ch: String, subscription: AbstractSubscription<Any>) {
        logger.debug("Register $ch")
        defaultSubscriptionMap[ch] = subscription
    }

    fun unregister(ch: String): AbstractSubscription<Any> {
        logger.debug("Unregister $ch")
        return defaultSubscriptionMap.remove(ch)!!
    }

    private fun buildHttpClientOptions(): HttpClientOptions {
        val options = HttpClientOptions()
        val rt = runtimeConfig
        if (rt.proxyHost != null && rt.proxyPort != null) {
            val po = ProxyOptions()
            po.host = rt.proxyHost
            po.port = rt.proxyPort!!
            options.proxyOptions = po
        }
        return options
    }

    /**
     * 用于突发的断线重连，服务器宕机等情况发生时，进行重启，并重新订阅所有的内容。
     */
    override suspend fun restart() {
        val subMapCopy = HashMap(defaultSubscriptionMap)
        val syncListCopy = ArrayList(synchronizeSubscriptionList)
        logger.debug("Restarting...")
        reconnect()
        logger.debug("Start to subscribe ${subMapCopy.values.joinToString(", ")}")
        syncListCopy.forEach { it.subscribe() }
        subMapCopy.values.forEach { it.subscribe() }
        defaultSubscriptionMap = ConcurrentHashMap(subMapCopy)
    }

    override suspend fun reconnect() {
        close()
        logger.debug("Waiting 2s for connecting.")
        delay(2000)
        connect()
        launchEventLoop()
    }

    override suspend fun connect() {
        logger.debug("Start to connect to $host")
        val hco = buildHttpClientOptions()
        val uri = URI.create(host)
        var port = uri.port
        if (uri.scheme == "wss") {
            hco.isSsl = true
        }
        if (uri.scheme == "wss" && uri.port == -1) {
            port = 443
        }
        state.set(EndpointStateEnum.CONNECTING)
        val rp = if (uri.query == null) {
            uri.path
        } else {
            "${uri.path}?${uri.query}"
        }
        ws = awaitResult { vertx.createHttpClient(hco).webSocket(port, uri.host, rp, it) }
        logger.debug("Connected to $host")
        ws.handler { receiveChannel.offer(it) }
        state.set(EndpointStateEnum.CONNECTED)
    }

    override suspend fun handleChannelEvents() {
        while (!receiveChannel.isEmpty) {
            val buffer = receiveChannel.receive()
            dispatch(buffer.bytes)
        }
        while (!sendChannel.isEmpty) {
            if (state.get() == EndpointStateEnum.INIT || state.get() == EndpointStateEnum.CLOSED) {
                connect()
            }
            while (state.get() == EndpointStateEnum.CONNECTING || state.get() == EndpointStateEnum.CLOSING) delay(100)
            val text = sendChannel.receive()
            ws.writeTextMessage(text)
            logger.debug("Websocket has sent $text")
        }
    }

    /**
     * 关闭该 dispatcher，并回收相关的资源。
     *
     * 子类可以复写该方法，但是一定要记得要在最后调用父类方法。
     */
    override suspend fun close() {
        logger.debug("Start to close the ${objSimpName(this)}.")
        state.set(EndpointStateEnum.CLOSING)
        eventLoop?.cancel(CancellationException("Active cancel this WebsocketDispatcher."))
        awaitResult<Void> { ws.close(it) }
        logger.debug("${objSimpName(this)} closed.")
        state.set(EndpointStateEnum.CLOSED)
        defaultSubscriptionMap.clear()
    }

    /**
     * 发送 pong 帧
     *
     * pong 帧的格式符合 RFC6455 的要求，目前只有 Binance 用到了这个方法。
     *
     * @param text String
     */
    override fun sendPong(text: String) {
        ws.writePing(Buffer.buffer(text))
        logger.debug("Websocket has sent pong frame: $text")
    }

    /**
     * 向 websocket 发送消息
     *
     * @receiver CoroutineScope
     * @param text String
     */
    override suspend fun send(text: String) {
        sendChannel.send(text)
    }

    open suspend fun handlePing(bytes: ByteArray): Boolean {
        return false
    }

    open suspend fun handleCommandResponse(elem: JsonElement) {
    }

    override fun <T : Any> newSubscription(channel: String, resolver: suspend (JsonElement, DefaultSubscription<T>) -> Unit): DefaultSubscription<T> {
        val sub = DefaultSubscription<T>(channel, this, resolver)
        @Suppress("UNCHECKED_CAST")
        register(channel, sub as AbstractSubscription<Any>)
        return sub
    }

    open fun <T : Any> newSynchronizeSubscription(resolver: (List<Any>, AbstractSubscription<T>) -> Unit): SynchronizedSubscription<T> {
        val sub = SynchronizedSubscription<T>(resolver)
        @Suppress("UNCHECKED_CAST")
        synchronizeSubscriptionList.add(sub as SynchronizedSubscription<Any>)
        return sub
    }

    open fun newDispatcher(): AbstractWebsocketDispatcher {
        throw NotImplementedError()
    }
}