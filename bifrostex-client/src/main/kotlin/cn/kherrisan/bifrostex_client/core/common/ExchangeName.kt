package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.exchange.binance.BinanceService
import cn.kherrisan.bifrostex_client.exchange.gateio.GateioService
import cn.kherrisan.bifrostex_client.exchange.huobi.HuobiService
import cn.kherrisan.bifrostex_client.exchange.kucoin.KucoinService
import cn.kherrisan.bifrostex_client.exchange.okex.OkexService

enum class ExchangeName(val exchangeServiceClass: Class<out ExchangeService>) {
    HUOBI(HuobiService::class.java),
    BINANCE(BinanceService::class.java),
    OKEX(OkexService::class.java),
    GATEIO(GateioService::class.java),
    KUCOIN(KucoinService::class.java)
}