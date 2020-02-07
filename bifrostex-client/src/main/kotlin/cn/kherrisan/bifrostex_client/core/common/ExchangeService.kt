package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.core.http.AuthenticationService
import cn.kherrisan.bifrostex_client.core.service.*
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking

/**
 * 货币交易所抽象类
 *
 * 每个ExchangeService都是一个Initializer，并且只会initialize一次
 *
 * @property vertx Vertx
 * @property publicHttpHost String
 * @property publicWsHost String
 * @property authHttpHost String
 * @property authWsHost String
 * @property rtConfig RuntimeConfiguration
 * @property name ExchangeName
 * @property initializedPromise (io.vertx.core.Promise<(kotlin.Any..kotlin.Any?)>..io.vertx.core.Promise<(kotlin.Any..kotlin.Any?)>?)
 * @property isInitialized Future<Any>
 * @property context MutableMap<String, Any>
 * @property dispatcher WebsocketDispatcher
 * @property spotAccountService SpotAccountService
 * @property spotMarketService SpotMarketService
 * @property spotTradingService SpotTradingService
 * @property metaInfo ExchangeMetaInfo
 * @constructor
 */
abstract class ExchangeService(val vertx: Vertx) : AbstractInitializer() {

    abstract var publicHttpHost: String

    abstract var publicWsHost: String

    open var authHttpHost: String = ""

    open var authWsHost: String = ""

    lateinit var rtConfig: RuntimeConfiguration

    lateinit var name: ExchangeName

    val context: MutableMap<String, Any> = HashMap()

    open override suspend fun allInitialize() {
        metaInfo
        initialize()
    }

    val dispatcher by lazy {
        initializeOnce()
        buildWebsocketDispatcher()
    }

    val spotMarketService by lazy {
        val sms = buildSpotMarketService()
        (sms as AbstractSpotMarketService).initializeOnce()
        sms
    }

    val spotTradingService by lazy {
        val sts = buildSpotTradingService()
        (sts as AbstractSpotTradingService).initializeOnce()
        sts
    }

    val marginTradingService by lazy {
        val mts = buildMarginTradingService()
        (mts as AbstractMarginTradingService).initializeOnce()
        mts
    }

    val metaInfo: ExchangeMetaInfo by lazy {
        buildMetaInfo()
    }

    open fun buildMetaInfo(): ExchangeMetaInfo {
        val meta = ExchangeMetaInfo(this)
        runBlocking {
            meta.init()
        }
        return meta
    }

    open fun buildAuthenticationService(): AuthenticationService {
        throw NotImplementedError()
    }

    open fun buildWebsocketDispatcher(): WebsocketDispatcher {
        throw NotImplementedError()
    }

    open fun buildSpotTradingService(): SpotTradingService {
        throw NotImplementedError()
    }

    open fun buildSpotMarketService(): SpotMarketService {
        throw NotImplementedError()
    }

    open fun buildMarginTradingService(): MarginTradingService {
        throw NotImplementedError()
    }

    abstract fun buildDataAdaptor(): ServiceDataAdaptor
}