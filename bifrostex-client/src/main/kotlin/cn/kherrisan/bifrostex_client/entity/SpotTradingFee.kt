package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.common.Open
import java.math.BigDecimal
import java.util.*

@Open
data class SpotTradingFee(
        val symbol: Symbol,
        val makerFee: BigDecimal,
        val takerFee: BigDecimal,
        val time: Date = MyDate()
)