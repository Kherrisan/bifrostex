package cn.kherrisan.bifrostex_engine.repository

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.Currency
import cn.kherrisan.bifrostex_engine.ExchangeSpotBalance
import cn.kherrisan.bifrostex_engine.SpotBalanceRepository
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

const val MONGO_EXCHANGE_SPOT_BALANCE = "exchange_spot_balance"

@Component
class VertxSpotBalanceRepository(val vertx: Vertx) {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var spotBalanceRepository: SpotBalanceRepository

    suspend fun insert(sb: ExchangeSpotBalance) {
        //spotbalance 不需要 update，如果有新的直接 insert。
        awaitBlocking {
            spotBalanceRepository.save(sb)
        }
    }

    suspend fun getByExchangeAndCurrency(ex: ExchangeName, c: Currency): ExchangeSpotBalance? {
        return awaitBlocking {
            val query = Query()
            query.addCriteria(Criteria.where("exchange").`is`(ex))
                    .addCriteria(Criteria.where("currency").`is`(c))
                    .with(Sort.by("time"))
                    .limit(1)
            mongoTemplate.findOne(query, ExchangeSpotBalance::class.java, MONGO_EXCHANGE_SPOT_BALANCE)
        }
    }
}