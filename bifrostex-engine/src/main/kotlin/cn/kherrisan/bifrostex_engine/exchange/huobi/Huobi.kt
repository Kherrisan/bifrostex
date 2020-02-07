package cn.kherrisan.bifrostex_engine.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_engine.core.AbstractExchange
import cn.kherrisan.bifrostex_engine.service.Future
import cn.kherrisan.bifrostex_engine.service.Margin
import cn.kherrisan.bifrostex_engine.service.Spot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Huobi : AbstractExchange() {

    override val name: ExchangeName = ExchangeName.HUOBI

    @Autowired
    override lateinit var config: HuobiConfig

    @Autowired
    override lateinit var spot: HuobiSpot

    override lateinit var future: Future
    override lateinit var margin: Margin
}