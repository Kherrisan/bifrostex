package cn.kherrisan.bifrostex_engine

import cn.kherrisan.bifrostex_client.core.common.ExchangeName
import cn.kherrisan.bifrostex_client.entity.BTC
import cn.kherrisan.bifrostex_engine.enumeration.TransactionType
import cn.kherrisan.bifrostex_engine.repository.SpotBalanceRepository
import cn.kherrisan.bifrostex_engine.repository.SpotOrderRepository
import cn.kherrisan.bifrostex_engine.repository.TransactionRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit4.SpringRunner
import kotlin.random.Random

@RunWith(SpringRunner::class)
@SpringBootTest
class RepositoryTest {

    @Autowired
    lateinit var balanceRepo: SpotBalanceRepository

    @Autowired
    lateinit var orderRepo: SpotOrderRepository

    @Autowired
    lateinit var transRepo: TransactionRepository

    val random = Random(System.currentTimeMillis())

    @Test
    fun `Insert mock transaction`() {
        val id = random.nextInt(10000)
        val trans = Transaction(ExchangeName.HUOBI, TransactionType.TRADE, random.nextFloat().toBigDecimal(), BTC, "Bilibili")
        transRepo.save(trans)
        val found = transRepo.findByIdOrNull(trans.tid)
        assert(trans.note == found!!.note)
        assert(trans.amount == found!!.amount)
    }
}