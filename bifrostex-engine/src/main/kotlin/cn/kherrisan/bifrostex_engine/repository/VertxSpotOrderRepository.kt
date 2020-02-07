package cn.kherrisan.bifrostex_engine.repository

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitBlocking
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

const val MONGO_EXCHANGE_SPOT_ORDER = "exchange_spot_order"

@Component
class VertxSpotOrderRepository(val vertx: Vertx) {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    suspend fun getByExchangeAndExOrderId(ex: ExchangeName, exOid: String): ExchangeSpotOrder? {
        return awaitBlocking {
            val q = Query()
            q.addCriteria(Criteria.where("exOid").`is`(exOid))
                    .addCriteria(Criteria.where("exchange").`is`(ex.name))
            mongoTemplate.findOne(q, ExchangeSpotOrder::class.java, MONGO_EXCHANGE_SPOT_ORDER)
        }
    }

    suspend fun getByOrderId(oid: String): ExchangeSpotOrder? {
        return awaitBlocking {
            val q = Query()
            q.addCriteria(Criteria.where("oid").`is`(oid))
            mongoTemplate.findOne(q, ExchangeSpotOrder::class.java, MONGO_EXCHANGE_SPOT_ORDER)
        }
    }

    suspend fun upsert(o: ExchangeSpotOrder) {
        awaitBlocking {
            val q = Query()
            q.addCriteria(Criteria.where("oid").`is`(o.oid))
            val d = Document()
            mongoTemplate.converter.write(o, d)
            val u = Update.fromDocument(d)
            mongoTemplate.upsert(q, u, "huobi_spot_order")
        }
    }
}