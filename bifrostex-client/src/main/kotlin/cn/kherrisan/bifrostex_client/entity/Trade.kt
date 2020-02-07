package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.Open
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import java.math.BigDecimal
import java.util.*

/**
 * 交易对象
 *
 * @property symbol Symbol 交易对
 * @property tid Int 交易id（交易所生成）
 * @property time Date 交易时间
 * @property amount BigDecimal 数量
 * @property price BigDecimal 金额
 * @property sideEnum TradeDirection 交易方向：买/卖
 * @constructor
 */
@Open
data class Trade(
        var symbol: Symbol,
        var tid: String,
        var time: Date,
        var amount: BigDecimal,
        var price: BigDecimal,
        var sideEnum: OrderSideEnum
)