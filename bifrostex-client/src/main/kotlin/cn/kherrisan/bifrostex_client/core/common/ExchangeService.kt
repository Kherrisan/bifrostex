package cn.kherrisan.bifrostex_client.core.common

import cn.kherrisan.bifrostex_client.core.service.MarginTradingService
import cn.kherrisan.bifrostex_client.core.service.SpotMarketService

abstract class ExchangeService {

    lateinit var runtimeConfig: RuntimeConfiguration

    lateinit var name: ExchangeName

    val context: MutableMap<String, Any> = HashMap()

    /**
     * 现货行情服务接口
     *
     * 子类重写时必须 Autowried 和 Lazy
     */
    abstract val spotMarketService: SpotMarketService

    /**
     * 现货交易服务接口
     *
     * 子类重写时必须 Autowried 和 Lazy
     */
    abstract val spotTradingService: SpotTradingService

    /**
     * 杠杆交易服务接口
     *
     * 子类重写时必须 Autowried 和 Lazy
     */
    abstract val marginTradingService: MarginTradingService
}