package cn.kherrisan.bifrostex_client.core.service

import cn.kherrisan.bifrostex_client.entity.Contract

/**
 * 期货市场行情接口
 */
interface FutureMarketService {

    suspend fun getContracts(): List<Contract>

}