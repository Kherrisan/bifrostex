package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.ExchangeMetaInfo
import cn.kherrisan.bifrostex_client.core.enumeration.AccountTypeEnum
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class HuobiMetaInfo @Autowired constructor(service: HuobiService) : ExchangeMetaInfo(service) {

    lateinit var accountIdMap: Map<AccountTypeEnum, String>
}