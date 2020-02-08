package cn.kherrisan.bifrostex_engine

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderTypeEnum
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_client.entity.SpotBalance
import cn.kherrisan.bifrostex_client.entity.SpotOrder
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_engine.core.AutoIncrement
import cn.kherrisan.bifrostex_engine.core.Decimal128
import cn.kherrisan.bifrostex_engine.enumeration.TransactionType
import cn.kherrisan.bifrostex_engine.repository.MONGO_EXCHANGE_SPOT_ORDER
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document("spot_balance")
data class ExchangeSpotBalance @PersistenceConstructor constructor(
        @AutoIncrement @Id val bid: Long,
        @Indexed val exchange: ExchangeName,
        @Indexed val currency: Currency,
        @Decimal128 val free: BigDecimal,
        @Decimal128 val frozen: BigDecimal,
        val time: Date
) {
    constructor(exchange: ExchangeName, currency: Currency, free: BigDecimal, frozen: BigDecimal, time: Date)
            : this(0, exchange, currency, free, frozen, time)

    constructor(ex: ExchangeName, sb: SpotBalance)
            : this(ex, sb.currency, sb.free, sb.frozen, sb.time)
}

/**
 *
 * @property oid Long
 * @property exchange ExchangeName
 * @property exOid String
 * @property symbol Symbol
 * @property time Date
 * @property amount BigDecimal
 * @property price BigDecimal
 * @property side OrderSideEnum
 * @property type OrderTypeEnum
 * @property state OrderStateEnum
 * @property bfr Boolean
 * @constructor
 */
@Document(MONGO_EXCHANGE_SPOT_ORDER)
data class ExchangeSpotOrder @PersistenceConstructor constructor(
        @AutoIncrement @Id val oid: Long,
        @Indexed val exchange: ExchangeName,
        val exOid: String,
        @Indexed val symbol: Symbol,
        val time: Date,
        @Decimal128 val amount: BigDecimal,
        @Decimal128 val price: BigDecimal,
        @Indexed val side: OrderSideEnum,
        val type: OrderTypeEnum,
        val state: OrderStateEnum,
        var bfr: Boolean
) {
    constructor(exchange: ExchangeName, exId: String, symbol: Symbol, createTime: Date, amount: BigDecimal, price: BigDecimal, side: OrderSideEnum, type: OrderTypeEnum, state: OrderStateEnum, bfr: Boolean)
            : this(0, exchange, exId, symbol, createTime, amount, price, side, type, state, bfr)

    constructor(exchange: ExchangeName, so: SpotOrder)
            : this(exchange, so.oid, so.symbol, so.createTime, so.amount, so.price, so.side, so.type, so.state, false)
}

@Document("transaction")
data class Transaction @PersistenceConstructor constructor(
        @AutoIncrement @Id val tid: Long,
        val exchange: ExchangeName,
        val type: TransactionType,
        @Decimal128 val amount: BigDecimal,
        val currency: Currency,
        val exId: String
) {
    constructor(exchange: ExchangeName, type: TransactionType, amount: BigDecimal, currency: Currency, exId: String)
            : this(0, exchange, type, amount, currency, exId)

}