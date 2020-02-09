package cn.kherrisan.bifrostex_client.exchange.poloniex

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.core.websocket.WebsocketDispatcher
import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PoloniexWebsocketDispatcher @Autowired constructor(val staticConfiguration: PoloniexStaticConfiguration) : WebsocketDispatcher() {
    override val host: String = staticConfiguration.spotMarketWsHost
    override val name: ExchangeName = ExchangeName.POLONIEX

    override suspend fun CoroutineScope.dispatch(bytes: ByteArray) {
        throw NotImplementedError()
    }
}