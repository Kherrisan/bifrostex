package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.Open
import java.math.BigDecimal
import java.util.*

/**
 *
 * @property symbol Symbol
 * @property time Date
 * @property open BigDecimal
 * @property close BigDecimal
 * @property high BigDecimal
 * @property low BigDecimal
 * @property volume BigDecimal
 * @constructor
 */
@Open
data class Kline(
        var symbol: Symbol,
        var time: Date,
        var open: BigDecimal,
        var close: BigDecimal,
        var high: BigDecimal,
        var low: BigDecimal,
        var volume: BigDecimal
)