package cn.kherrisan.bifrostex_client.core.common

interface Initializer {

    /**
     * 初始化时要做的事情
     */
    suspend fun allInitialize()

    /**
     * 等待初始化的完成
     */
    suspend fun awaitInitialization()

}