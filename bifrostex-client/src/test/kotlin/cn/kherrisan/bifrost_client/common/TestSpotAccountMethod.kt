package cn.kherrisan.bifrost_client.common

import cn.kherrisan.bifrostex_client.core.common.RuntimeConfiguration

abstract class TestSpotAccountMethod : TestExchangeMethod() {

    override val rtConfig: RuntimeConfiguration = RuntimeConfiguration(
            apiKey = "zrfc4v5b6n-f2c7f45f-b338b7f4-8412c",
            secretKey = "f421cfcf-b422e894-3a57aedd-7bb0f"
    )

//    @Test
//    fun testGetTransactionHistory() {
//        runBlocking {
//            spotAccount.getTransactionHistory().forEach {
//                logger.info("${it}")
//            }
//        }
//    }

//    @Test
//    fun testGetBalance() {
//        runBlocking {
//            spotAccount.getBalance().entries.forEach {
//                logger.info("${it.key}:${it.value}")
//            }
//        }
//    }
}