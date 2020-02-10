package cn.kherrisan.bifrostex_client.core.common

/**
 * ExchangeService 工厂类
 *
 * 这个类不需要 Spring 进行管理
 */
object ExchangeFactory {

    /**
     * ExchangeService 工厂方法
     *
     * @param name ExchangeName
     * @param config RuntimeConfiguration
     * @return ExchangeService
     */
    fun build(name: ExchangeName = ExchangeName.HUOBI, config: RuntimeConfiguration = RuntimeConfiguration()): ExchangeService {
        val service = SpringContainer[name.exchangeServiceClass]
        RuntimeConfigContainer[name] = config
        service.runtimeConfig = config
        service.name = name
        return service
    }
}