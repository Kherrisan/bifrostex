package cn.kherrisan.bifrostex_client.exchange.kucoin

data class KucoinInstanceServer(
        val url: String,
        val pingInterval: Int,
        val pingTimeout: Int,
        val token: String
)