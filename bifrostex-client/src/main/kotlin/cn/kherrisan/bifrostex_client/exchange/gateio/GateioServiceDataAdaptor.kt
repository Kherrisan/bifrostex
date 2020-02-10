package cn.kherrisan.bifrostex_client.exchange.gateio

import cn.kherrisan.bifrostex_client.core.common.AbstractServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.Compound
import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderTypeEnum
import cn.kherrisan.bifrostex_client.entity.Symbol
import com.google.gson.JsonElement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class GateioServiceDataAdaptor @Autowired constructor(metaInfo: GateioMetaInfo) : AbstractServiceDataAdaptor(metaInfo) {

    override fun string(symbol: Symbol): String {
        return "${symbol.base}_${symbol.quote}".toUpperCase()
    }

    override fun string(periodEnum: KlinePeriodEnum): String {
        return periodEnum.toSeconds().toString()
    }

    override fun string(date: Date): String {
        throw NotImplementedError()
    }

    override fun orderSide(str: String): OrderSideEnum {
        return OrderSideEnum.valueOf(str.toUpperCase())
    }

    override fun symbol(str: String): Symbol {
        val mid = str.indexOf("_")
        return Symbol(str.substring(0, mid).toLowerCase(), str.substring(mid + 1).toLowerCase())
    }

    override fun date(str: String): Date {
        return MyDate(str.toLong() * 1000)
    }

    override fun orderType(str: String): OrderTypeEnum {
        throw NotImplementedError()
    }

    override fun orderState(str: String): OrderStateEnum {
        throw NotImplementedError()
    }

    override fun string(state: OrderStateEnum): String {
        throw NotImplementedError()
    }

    override fun bigDecimal(e: JsonElement): BigDecimal {
        return e.asString.toBigDecimal()
    }
}