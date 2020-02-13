package cn.kherrisan.bifrostex_client.core.common

import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope

fun DefaultScope(): CoroutineScope = CoroutineScope(VertxContainer.vertx().dispatcher())