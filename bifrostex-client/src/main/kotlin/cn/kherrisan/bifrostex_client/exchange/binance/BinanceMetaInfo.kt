package cn.kherrisan.bifrostex_client.exchange.binance

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BinanceMetaInfo @Autowired constructor(service: BinanceService) : ExchangeMetaInfo(service)