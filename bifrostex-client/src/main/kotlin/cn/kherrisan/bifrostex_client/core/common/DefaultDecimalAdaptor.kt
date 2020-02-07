package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_client.entity.Symbol

class DefaultDecimalAdaptor(val service: ExchangeService) : DecimalAdaptor {
    override fun sizeIncrement(currency: Currency?): Int {
        return service.metaInfo.currencyMetaInfo[currency]?.sizeIncrement ?: 4
    }

    override fun sizeIncrement(symbol: Symbol?): Int {
        return service.metaInfo.symbolMetaInfo[symbol]?.sizeIncrement ?: 4
    }

    override fun priceIncrement(symbol: Symbol?): Int {
        return service.metaInfo.symbolMetaInfo[symbol]?.priceIncrement ?: 4
    }

    override fun volumeIncrement(symbol: Symbol?): Int {
        return service.metaInfo.symbolMetaInfo[symbol]?.volumeIncrement ?: 4
    }
}