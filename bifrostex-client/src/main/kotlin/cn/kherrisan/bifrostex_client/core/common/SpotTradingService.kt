package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.core.service.SpotMarginTradingService
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_client.entity.SpotBalance

interface SpotTradingService : SpotMarginTradingService {

    /**
     * 查询账户所有币种余额
     *
     * 会自动过滤余额过小的币种
     *
     * @return Map<Currency, Balance>
     */
    suspend fun getBalance(): Map<Currency, SpotBalance>
}