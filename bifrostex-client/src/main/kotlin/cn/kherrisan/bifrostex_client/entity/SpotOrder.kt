package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.Open
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderTypeEnum
import java.math.BigDecimal
import java.util.*

/**
 * 现货订单
 *
 * 充当用户下单时的返回值，一般只有oid这个字段是有效的，其他字段都是用户已知的。更加详细的订单信息见SpotOrderDetail类。
 *
 * @property oid String 订单id：只有这个字段是服务器给出的
 * @property symbol Symbol 对应现货交易对
 * @property side TradeDirectionEnum 下单交易方向
 * @property price BigDecimal 出价，如果是市价单，那么
 * @property amount BigDecimal 基础货币（base）数量
 * @property filledAmount BigDecimal 已成交的数量
 * @property createTime Date 订单创建时间，如果系统没有在下单时返回下单时间，那么本程序会自己赋一个当前时间。实际的订单创建时间（下单时间）以detail为准
 * @property state OrderStateEnum 订单状态
 * @property type OrderTypeEnum 市价/限价
 * @constructor
 */
@Open
data class SpotOrder(
        val oid: String,
        val symbol: Symbol,
        val side: OrderSideEnum,
        val price: BigDecimal,
        val amount: BigDecimal,
        val filledAmount: BigDecimal,
        val createTime: Date,
        val state: OrderStateEnum,
        val type: OrderTypeEnum
)