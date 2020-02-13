package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.core.service.SpotMarginTradingService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_client.entity.SpotBalance
import cn.kherrisan.bifrostex_client.entity.SpotOrder
import cn.kherrisan.bifrostex_client.entity.Symbol

interface SpotTradingService : SpotMarginTradingService {

    /**
     * 查询账户所有币种余额
     *
     * 会自动过滤余额过小的币种
     *
     * @return Map<Currency, Balance>
     */
    suspend fun getBalance(): Map<Currency, SpotBalance>

    /**
     * 订阅账户余额的快照
     *
     * @param symbol Symbol
     * @return Subscription<SpotBalance>
     */
    suspend fun subscribeBalance(symbol: Symbol?): Subscription<SpotBalance>

    /**
     * 订阅账户订单增量数据
     *
     * @param symbol Symbol
     * @return Subscription<SpotOrder>
     */
    suspend fun subscribeOrder(symbol: Symbol): Subscription<SpotOrder>
}