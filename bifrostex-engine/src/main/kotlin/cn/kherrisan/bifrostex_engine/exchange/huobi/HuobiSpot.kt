package cn.kherrisan.bifrostex_engine.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.entity.Symbol
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import cn.kherrisan.bifrostex_engine.InvalidArgumentException
import cn.kherrisan.bifrostex_engine.service.AbstractSpot
import kotlinx.coroutines.CoroutineScope
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class HuobiSpot(@Autowired exchange: Huobi) : AbstractSpot(exchange) {
    override val name: ExchangeName = ExchangeName.HUOBI

    /**
     * 获得订单列表
     *
     * Huobi 对查询起止时间和窗口大小有限制，查询窗口最早开始时间为 180 天之前，最晚结束时间为当前时间，最大查询窗口为 2 天，其中 CANCELLED 订单的
     * 窗口最早开始时间为 1 天。
     * 说白了就是 Huobi 会定时清理数据库，180 天前的订单全部查不到，并且撤销的订单会清理的更加频繁。
     *
     * @receiver CoroutineScope
     * @param symbol Symbol
     * @param start Date?
     * @param end Date?
     * @param state OrderStateEnum?
     * @return List<ExchangeSpotOrder>
     */
    override suspend fun CoroutineScope.getOrders(symbol: Symbol, start: Date?, end: Date?, state: OrderStateEnum?): List<ExchangeSpotOrder> {
        var notNullEnd = end ?: Date()
        var notNullStart = DateUtils.addDays(notNullEnd, -1)
        if (notNullStart > Date() || notNullEnd > Date()) {
            throw InvalidArgumentException(notNullStart, notNullEnd)
        }
        val oldest = spotOrderRepository.getOldest(ExchangeName.HUOBI, symbol)
        if (oldest == null || notNullStart < oldest.time) {
            //需要查 api
            //最早只能查 180 天的数据
            notNullStart = maxOf(DateUtils.addDays(Date(), -179), notNullStart)
            var itrEnd = DateUtils.addDays(notNullStart, 2)
            if (oldest != null) {
                notNullEnd = minOf(oldest.time, notNullEnd)
            }
            while (itrEnd <= notNullEnd) {
                spotTradingService.getOrders(symbol, notNullStart, itrEnd, size = 100)
                        .map { ExchangeSpotOrder(ExchangeName.HUOBI, it) }
                        .forEach {
                            spotOrderRepository.upsertByExoid(it)
                        }
                itrEnd = DateUtils.addDays(itrEnd, 2)
            }
        }
        return spotOrderRepository.get(ExchangeName.HUOBI, symbol, start, end, state)
    }
}