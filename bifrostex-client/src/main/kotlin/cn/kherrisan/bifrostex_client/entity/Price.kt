package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.Open
import java.math.BigDecimal

@Open
data class Price(val value: BigDecimal, val scale: Int) : Comparable<Price> {

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is Price && value.compareTo(other.value) == 0
    }

    override fun compareTo(other: Price): Int {
        return value.compareTo(other.value)
    }
}