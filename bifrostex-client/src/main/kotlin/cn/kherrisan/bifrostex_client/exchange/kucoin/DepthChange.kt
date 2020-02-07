package cn.kherrisan.bifrostex_client.exchange.kucoin

import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import java.math.BigDecimal

data class DepthChange(val price: BigDecimal, val size: BigDecimal, val seq: Long, val side: OrderSideEnum)