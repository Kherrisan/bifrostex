package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.MyDate
import cn.kherrisan.bifrostex_client.core.common.Open
import java.math.BigDecimal
import java.util.*

/**
 *
 * @property currency Currency
 * @property free BigDecimal
 * @property frozen BigDecimal
 * @property time Date
 * @constructor
 */
@Open
data class SpotBalance(val currency: Currency,
                       var free: BigDecimal = 0f.toBigDecimal(),
                       var frozen: BigDecimal = 0f.toBigDecimal(),
                       val time: Date = MyDate())