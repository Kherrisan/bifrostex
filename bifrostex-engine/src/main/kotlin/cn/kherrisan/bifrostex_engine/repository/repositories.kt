package cn.kherrisan.bifrostex_engine.repository

import cn.kherrisan.bifrostex_engine.ExchangeSpotBalance
import cn.kherrisan.bifrostex_engine.ExchangeSpotOrder
import cn.kherrisan.bifrostex_engine.Transaction
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotBalanceRepository : MongoRepository<ExchangeSpotBalance, Long> {

}

@Repository
interface SpotOrderRepository : MongoRepository<ExchangeSpotOrder, Long> {

}

@Repository
interface TransactionRepository : MongoRepository<Transaction, Long> {

}