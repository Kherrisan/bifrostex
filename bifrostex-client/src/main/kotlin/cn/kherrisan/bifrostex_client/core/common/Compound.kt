package cn.kherrisan.bifrostex_client.core.common

/**
 * 复合操作注解
 *
 * 本程序默认提供的操作方法都是针对交易所提供的某一个 API 的，即每个方法内部会发送一起 HTTP 请求（也不会不发送请求而去使用缓存数据，也不会多次发送请求）。
 * 但由于各个交易所提供的部分 API 的语义和参数要求的确存在不小的差别。例如，针对查询列表的接口，Okex 不提供日期范围参数 ，而只允许接收 id 范围参数。
 * 针对这样的问题，可能的解决方案是先随便查一个 id 出来，然后根据这个项目的日期往前或者往后查。
 * 若是要为这些存在差别的接口提供统一的规约，把差别隐藏在方法体内部，一种常见的做法就是发起多次 HTTP 请求，将结果进行整合之后返回出来。
 * 本注解就是标注了这些需要复合 HTTP 请求结果的方法。
 */
annotation class Compound {

}