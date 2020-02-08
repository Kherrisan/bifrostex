package cn.kherrisan.bifrostex_engine.repository

import cn.kherrisan.bifrostex_engine.Transaction
import io.vertx.kotlin.coroutines.awaitBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class VertxTransactionRepository {

    @Autowired
    lateinit var mongoTemplte: TransactionRepository

    suspend fun insert(t: Transaction) {
        awaitBlocking {
            mongoTemplte.save(t)
        }
    }
}