package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.entity.*
import java.math.BigDecimal

class DefaultTestUtils(val symbolMetaInfo: SymbolMetaInfo) : TestUtils {

    override fun assert(trade: Trade) {
        throw NotImplementedError()
    }

    override fun assert(klines: List<Kline>) {
        throw NotImplementedError()
    }

    override fun assert(ticker: Ticker) {
        throw NotImplementedError()
    }

    override fun assert(depth: Depth) {
        throw NotImplementedError()
    }

    override fun assertPricePrecision(price: BigDecimal) {
        throw NotImplementedError()
    }

    override fun assertSizePrecision(size: BigDecimal) {
        throw NotImplementedError()
    }

    override fun assertVolumePrecision(vol: BigDecimal) {
        throw NotImplementedError()
    }
}