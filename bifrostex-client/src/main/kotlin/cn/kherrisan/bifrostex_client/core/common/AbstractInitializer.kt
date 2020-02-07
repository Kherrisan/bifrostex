package cn.kherrisan.bifrostex_client.core.common

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager

abstract class AbstractInitializer(val parent: AbstractInitializer? = null) : Initializer, CoroutineScope by DefaultScope(Vertx.vertx()) {

    private val promise = Promise.promise<Any>()
    private val future: Future<Any> = promise.future()
    private var isInitialized: Boolean = false
    private val instance by lazy { this }
    val logger = LogManager.getLogger(this::class.java)

    open suspend fun initialize() {}

    /**
     * 异步地完成初始化操作
     */
    fun initializeOnce() {
        launch(Vertx.vertx().dispatcher(), CoroutineStart.ATOMIC) {
            withContext(singleThread) {
                if (!isInitialized) {
                    isInitialized = true
                    if (parent != null) {
                        parent.initializeOnce()
                    }
                    logger.debug("Initializing ${instance.javaClass.simpleName}@${instance.hashCode()}, parent is ${parent?.javaClass?.simpleName}@${parent.hashCode()}")
                    allInitialize()
                    promise.complete()
                    logger.debug("Initialized ${instance.javaClass.simpleName}@${instance.hashCode()}")
                }
            }
        }
    }

    override suspend fun awaitInitialization() {
        logger.debug("Await initializing ${objSimpName(this)}.")
        parent?.awaitInitialization()
        future.await()
        logger.debug("Await initialized ${objSimpName(this)}.")
    }
}