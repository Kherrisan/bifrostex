package cn.kherrisan.bifrostex_engine.service

import cn.kherrisan.bifrostex_client.core.common.ServiceDataAdaptor
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_client.entity.Symbol
import java.math.BigDecimal

abstract class AbstractMarket(val adaptor: ServiceDataAdaptor) {

    fun price(d: BigDecimal, symbol: Symbol): BigDecimal {
        return adaptor.price(d, symbol)
    }

    fun amount(d: BigDecimal, symbol: Symbol): BigDecimal {
        return adaptor.size(d, symbol)
    }

    fun amount(d: BigDecimal, c: Currency): BigDecimal {
        return adaptor.size(d, c)
    }

    fun volume(d: BigDecimal, symbol: Symbol): BigDecimal {
        return adaptor.volume(d, symbol)
    }
}