package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.entity.*
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExchangeMetaInfo(val service: ExchangeService) : CoroutineScope by DefaultScope(service.vertx) {

    lateinit var currencyList: List<Currency>
    lateinit var marginMetaInfo: MutableMap<Symbol, MarginInfo>
    val symbolMetaInfo: MutableMap<Symbol, SymbolMetaInfo> = HashMap()
    val currencyMetaInfo: MutableMap<Currency, CurrencyMetaInfo> = HashMap()

    suspend fun initMarginMetaInfo(): Job = launch(service.vertx.dispatcher()) {
        marginMetaInfo = service.marginTradingService.getMarginInfo().toMutableMap()
    }

    suspend fun initCurrencyList(): Job = launch(service.vertx.dispatcher()) {
        currencyList = service.spotMarketService.getCurrencies()
    }

    suspend fun initSymbolMetaInfo(): Job = launch(service.vertx.dispatcher()) {
        service.spotMarketService.getSymbolMetaInfo().forEach {
            symbolMetaInfo[it.symbol] = it
            if (!currencyMetaInfo.containsKey(it.symbol.base)) {
                currencyMetaInfo[it.symbol.base] = CurrencyMetaInfo(it.symbol.base, 0)
            }
            currencyMetaInfo[it.symbol.base]!!.smallerSizeIncrement(it.sizeIncrement)
            if (!currencyMetaInfo.containsKey(it.symbol.quote)) {
                currencyMetaInfo[it.symbol.quote] = CurrencyMetaInfo(it.symbol.quote, 0)
            }
            currencyMetaInfo[it.symbol.quote]!!.smallerSizeIncrement(it.volumeIncrement)
        }
    }

    suspend fun init() {
        for (i in listOf(initCurrencyList(),
                initSymbolMetaInfo())) {
            i.join()
        }
    }
}