package cn.kherrisan.bifrostex_client.entity

import cn.kherrisan.bifrostex_client.core.common.Open

@Open
data class OrderResult(
        val oid: String,
        val result: Boolean = true,
        val errMsg: String = ""
)