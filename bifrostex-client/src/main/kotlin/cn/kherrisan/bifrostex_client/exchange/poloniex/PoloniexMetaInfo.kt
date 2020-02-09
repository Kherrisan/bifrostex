package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PoloniexMetaInfo @Autowired constructor(service: PoloniexService) : ExchangeMetaInfo(service)