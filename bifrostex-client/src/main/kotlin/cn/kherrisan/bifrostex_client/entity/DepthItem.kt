package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.Open
import java.math.BigDecimal

/**
 * 市场交易深度
 *
 * @property price BigDecimal 价格
 * @property amount BigDecimal 待成交币种个数（基础货币）
 * @constructor
 */
@Open
data class DepthItem(val price: BigDecimal, var amount: BigDecimal) : Comparable<DepthItem> {
    override fun compareTo(other: DepthItem): Int {
        return price.compareTo(other.price)
    }
}