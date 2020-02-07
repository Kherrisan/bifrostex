package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_client.entity.Ticker
import java.math.BigDecimal

/**
 *
 * @property id Int Id of the currency pair.
 * @constructor
 */
class PoloniexTicker(symbol: Symbol, amount: BigDecimal, vol: BigDecimal, open: BigDecimal, close: BigDecimal, high: BigDecimal, low: BigDecimal, bid: BigDecimal, ask: BigDecimal, var id: Int) : Ticker(symbol, amount, vol, open, close, high, low, bid, ask)