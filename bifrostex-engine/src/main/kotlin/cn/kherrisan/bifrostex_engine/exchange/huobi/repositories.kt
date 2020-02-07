package cn.kherrisan.bifrostex_engine.exchange.huobi

import cn.kherrisan.bifrostex_client.entity.SpotOrder
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface HuobiSpotOrderRepository : MongoRepository<SpotOrder, String> {
}
