package cn.kherrisan.bifrostex_client.core.common

import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope

fun DefaultCoroutineScope(): CoroutineScope = CoroutineScope(VertxContainer.vertx().dispatcher())