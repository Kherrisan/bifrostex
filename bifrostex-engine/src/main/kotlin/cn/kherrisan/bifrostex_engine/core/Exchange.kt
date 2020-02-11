package cn.kherrisan.bifrostex_engine.core

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.common.ExchangeRuntimeConfig
import cn.kherrisan.bifrostex_client.core.common.ExchangeService
import cn.kherrisan.bifrostex_engine.service.Future
import cn.kherrisan.bifrostex_engine.service.Margin
import cn.kherrisan.bifrostex_engine.service.Spot

interface Exchange {
    val name: ExchangeName
    val service: ExchangeService
    val config: ExchangeRuntimeConfig
    val spot: Spot
    val future: Future
    val margin: Margin
}