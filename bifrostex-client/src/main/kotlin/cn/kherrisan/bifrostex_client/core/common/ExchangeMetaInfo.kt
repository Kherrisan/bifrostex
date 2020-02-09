package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.entity.*
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct

open class ExchangeMetaInfo() {

    lateinit var currencyList: List<Currency>
    lateinit var marginMetaInfo: MutableMap<Symbol, MarginInfo>
    val symbolMetaInfo: MutableMap<Symbol, SymbolMetaInfo> = HashMap()
    val currencyMetaInfo: MutableMap<Currency, CurrencyMetaInfo> = HashMap()

    @Autowired
    lateinit var vertx: Vertx

    @PostConstruct
    fun initMetaInfo() {
        runBlocking {
            for (i in listOf(initCurrencyList(),
                    initSymbolMetaInfo())) {
                i.join()
            }
        }
    }

    suspend fun CoroutineScope.initMarginMetaInfo(): Job = launch(vertx.dispatcher()) {
//        marginMetaInfo = service.marginTradingService.getMarginInfo().toMutableMap()
    }

    suspend fun CoroutineScope.initCurrencyList(): Job = launch(vertx.dispatcher()) {
//        currencyList = service.spotMarketService.getCurrencies()
    }

    suspend fun CoroutineScope.initSymbolMetaInfo(): Job = launch(vertx.dispatcher()) {
//        service.spotMarketService.getSymbolMetaInfo().forEach {
//            symbolMetaInfo[it.symbol] = it
//            if (!currencyMetaInfo.containsKey(it.symbol.base)) {
//                currencyMetaInfo[it.symbol.base] = CurrencyMetaInfo(it.symbol.base, 0)
//            }
//            currencyMetaInfo[it.symbol.base]!!.smallerSizeIncrement(it.sizeIncrement)
//            if (!currencyMetaInfo.containsKey(it.symbol.quote)) {
//                currencyMetaInfo[it.symbol.quote] = CurrencyMetaInfo(it.symbol.quote, 0)
//            }
//            currencyMetaInfo[it.symbol.quote]!!.smallerSizeIncrement(it.volumeIncrement)
//        }
    }
}