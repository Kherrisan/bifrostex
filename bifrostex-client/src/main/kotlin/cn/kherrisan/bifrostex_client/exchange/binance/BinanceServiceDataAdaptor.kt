package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.AbstractServiceDataAdaptor
import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.common.UNSUPPORTED_KLINE_PERIOD_ENUM
import cn.kherrisan.bifrostex_client.core.enumeration.KlinePeriodEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.Symbol
import java.util.*

class BinanceServiceDataAdaptor(service: BinanceService) : AbstractServiceDataAdaptor(service) {
    override val klinePeriodMap: Map<KlinePeriodEnum, String> = mapOf(
            KlinePeriodEnum._1MIN to "1m",
            KlinePeriodEnum._3MIN to "3m",
            KlinePeriodEnum._5MIN to "5m",
            KlinePeriodEnum._15MIN to "15m",
            KlinePeriodEnum._30MIN to "30m",
            KlinePeriodEnum._60MIN to "1h",
            KlinePeriodEnum._2HOUR to "2h",
            KlinePeriodEnum._4HOUR to "4h",
            KlinePeriodEnum._6HOUR to "6h",
            KlinePeriodEnum._8HOUR to "8h",
            KlinePeriodEnum._12HOUR to "12h",
            KlinePeriodEnum._1DAY to "1d",
            KlinePeriodEnum._3DAY to "3d",
            KlinePeriodEnum._1WEEK to "1w",
            KlinePeriodEnum._1MON to "1M"
    )

    override val orderStateMap: Map<String, OrderStateEnum> = mapOf(
            "NEW" to OrderStateEnum.SUBMITTED,
            "PARTIALLY_FILLED" to OrderStateEnum.PARTIAL_FILLED,
            "FILLED" to OrderStateEnum.FILLED,
            "CANCELED" to OrderStateEnum.CANCELED
    )

    override fun string(symbol: Symbol): String {
        return symbol.nameWithoutSlash().toUpperCase()
    }

    override fun string(periodEnum: KlinePeriodEnum): String {
        return klinePeriodMap[periodEnum] ?: error(UNSUPPORTED_KLINE_PERIOD_ENUM(periodEnum))
    }

    override fun string(date: Date): String {
        return date.time.toString()
    }

    /**
     * isBuyerMaker
     *
     * @param str String
     * @return TradeDirectionEnum
     */
    override fun orderSide(str: String): OrderSideEnum {
        return if (str.toBoolean()) {
            OrderSideEnum.BUY
        } else {
            OrderSideEnum.SELL
        }
    }

    override fun symbol(str: String): Symbol {
        return Symbol.parse(str, service.metaInfo.currencyList)
    }

    override fun date(str: String): Date {
        return MyDate(str.toLong())
    }

    override fun orderState(str: String): OrderStateEnum {
        return orderStateMap[str]!!
    }

    /**
     * Binance没有CREATED状态
     *
     * @param state OrderStateEnum
     * @return String
     */
    override fun string(state: OrderStateEnum): String {
        return orderStateMap.entries.find { it.value.equals(state) }!!.key
    }
}