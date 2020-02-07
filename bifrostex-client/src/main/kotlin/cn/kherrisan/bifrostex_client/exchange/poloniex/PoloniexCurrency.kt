package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.entity.Currency

class PoloniexCurrency(name: String, var id: Int) : Currency(name) {
    override fun toString(): String = "$name:$id"
}