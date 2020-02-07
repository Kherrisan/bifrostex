package cn.kherrisan.bifrostex_client.core.common

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope

fun DefaultScope(vertx: Vertx): CoroutineScope = CoroutineScope(vertx.dispatcher())