package cn.kherrisan.bifrostex_engine

import cn.kherrisan.bifrostex_client.entity.Symbol
import io.vertx.redis.RedisOptions

val REDIS_OPTION = RedisOptions()

const val COMMON_SYMBOL_DATA = "COMMON_SYMBOL_DATA"
const val BUS_STORE_TICKER = "BUS_STORE_TICKER"
const val BUS_TELEGRAM_MESSAGE = "BUS_TELEGRAM_MESSAGE"

val COMMON_SYMBOL_LIST = listOf(
        Symbol("btc", "usdt"),
        Symbol("eth", "usdt"),
        Symbol("xrp", "btc")
)