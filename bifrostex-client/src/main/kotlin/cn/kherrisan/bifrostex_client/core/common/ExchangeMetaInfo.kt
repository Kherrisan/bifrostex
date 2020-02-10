package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.entity.*

open class ExchangeMetaInfo() {

    lateinit var currencyList: List<Currency>
    lateinit var marginMetaInfo: MutableMap<Symbol, MarginInfo>
    val symbolMetaInfo: MutableMap<Symbol, SymbolMetaInfo> = HashMap()
    val currencyMetaInfo: MutableMap<Currency, CurrencyMetaInfo> = HashMap()
}