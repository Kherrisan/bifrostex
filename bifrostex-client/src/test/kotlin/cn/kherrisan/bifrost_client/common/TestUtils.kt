package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.entity.Depth
import cn.kherrisan.bifrostex_client.entity.Kline
import cn.kherrisan.bifrostex_client.entity.Ticker
import cn.kherrisan.bifrostex_client.entity.Trade
import java.math.BigDecimal

interface TestUtils {

    fun assert(depth: Depth)

    fun assert(trade: Trade)

    fun assert(klines: List<Kline>)

    fun assert(ticker: Ticker)

    fun assertPricePrecision(price: BigDecimal)

    fun assertSizePrecision(size: BigDecimal)

    fun assertVolumePrecision(vol: BigDecimal)

}