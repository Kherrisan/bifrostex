package cn.kherrisan.bifrostex_client.exchange.huobi

import cn.kherrisan.bifrostex_client.core.common.GET
import cn.kherrisan.bifrostex_client.core.common.POST
import cn.kherrisan.bifrostex_client.core.common.sortedUrlEncode
import cn.kherrisan.bifrostex_client.core.enumeration.AccountTypeEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderSideEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderStateEnum
import cn.kherrisan.bifrostex_client.core.enumeration.OrderTypeEnum
import cn.kherrisan.bifrostex_client.core.http.HttpMediaTypeEnum
import cn.kherrisan.bifrostex_client.core.service.AbstractSpotTradingService
import cn.kherrisan.bifrostex_client.core.websocket.Subscription
import cn.kherrisan.bifrostex_client.entity.*
import cn.kherrisan.bifrostex_client.entity.Currency
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct

/**
 * 火币现货交易服务实现类。
 *
 * @constructor
 */
@Component
class HuobiSpotTradingService @Autowired constructor(
        staticConfig: HuobiStaticConfiguration,
        dataAdaptor: HuobiServiceDataAdaptor,
        val metaInfo: HuobiMetaInfo
) : AbstractSpotTradingService(staticConfig, dataAdaptor, HuobiAuthenticateService(staticConfig.spotTradingHttpHost)) {

    @PostConstruct
    fun initAccountIdMap() {
        runBlocking {
            metaInfo.accountIdMap = getAccountIds()
        }
    }

    override fun checkResponse(http: HttpResponse<Buffer>): JsonElement {
        val obj = JsonParser.parseString(http.bodyAsString()).asJsonObject
        if (obj.has("status") && obj["status"].asString == "error") {
            logger.error(obj)
            error(obj)
        }
        return obj
    }

    /**
     * 对subpath进行签名
     *
     * Huobi的签名方式比较特别，是把一些和身份认证相关的参数以及httpget请求参数放在一起求sha，然后和其他份认证相关的参数一起放回到url里面去。
     *
     * @param subPath String
     * @param method String
     * @param params MutableMap<String, Any>
     * @return String
     */
    private fun buildSignedSubpath(subPath: String, method: String, params: MutableMap<String, Any> = mutableMapOf()): String {
        auth().signedHttpRequest(method, subPath, params, mutableMapOf())
        return "$subPath?${sortedUrlEncode(params)}"
    }

    override suspend fun createOrder(symbol: Symbol, price: BigDecimal, amount: BigDecimal, side: OrderSideEnum, type: OrderTypeEnum): TransactionResult {
        val signedSubpath = buildSignedSubpath("/v1/order/orders/place", POST)
        val params = mutableMapOf<String, Any>(
                "account-id" to metaInfo.accountIdMap[AccountTypeEnum.SPOT]!!,
                "symbol" to string(symbol),
                "type" to "${side.toString().toLowerCase()}-${type.toString().toLowerCase()}",
                "amount" to amount.toString(),
                "source" to "api" // 现货交易填写“api”，杠杆交易填写“margin-api”
        )
        // 限价
        if (type == OrderTypeEnum.LIMIT) {
            params["price"] = price.toString()
        }
        val resp = post(authUrl(signedSubpath), params = params)
        val it = jsonObject(resp)
        return TransactionResult(it["data"].asString)
    }

    /**
     *
     * @param symbol Symbol
     * @param price BigDecimal
     * @param amount BigDecimal
     * @return OrderResult
     */
    override suspend fun limitBuy(symbol: Symbol, price: BigDecimal, amount: BigDecimal): TransactionResult {
        return createOrder(symbol, price, amount, OrderSideEnum.BUY, OrderTypeEnum.LIMIT)
    }

    override suspend fun limitSell(symbol: Symbol, price: BigDecimal, amount: BigDecimal): TransactionResult {
        return createOrder(symbol, price, amount, OrderSideEnum.SELL, OrderTypeEnum.LIMIT)
    }

    /**
     *
     * @param symbol Symbol
     * @param amount BigDecimal
     * @param volume BigDecimal quote金额
     * @return OrderResult
     */
    override suspend fun marketBuy(symbol: Symbol, amount: BigDecimal?, volume: BigDecimal?): TransactionResult {
        return createOrder(symbol, BigDecimal.ZERO, volume
                ?: error("Market buy volume can't be null!"), OrderSideEnum.BUY, OrderTypeEnum.MARKET)
    }

    /**
     *
     * @param symbol Symbol
     * @param amount BigDecimal base金额
     * @return OrderResult
     */
    override suspend fun marketSell(symbol: Symbol, amount: BigDecimal): TransactionResult {
        return createOrder(symbol, BigDecimal.ZERO, amount, OrderSideEnum.SELL, OrderTypeEnum.MARKET)
    }

    override suspend fun getOrderDetail(oid: String, symbol: Symbol): SpotOrder {
        val resp = signedGet(authUrl("/v1/order/orders/$oid"))
        val it = jsonObject(resp)
        val data = it["data"].asJsonObject
        val sym = symbol(data["symbol"].asString)
        return SpotOrder(
                oid,
                sym,
                orderSide(data["type"].asString),
                price(data["price"], sym),
                size(data["amount"], sym),
                size(data["field-amount"], sym),
                date(data["created-at"].asLong.toString()),
                orderState(data["state"].asString),
                orderType(data["type"].asString)
        )
    }

    override suspend fun getOpenOrders(symbol: Symbol, size: Int): List<SpotOrder> {
        val params = mapOf("account-id" to metaInfo.accountIdMap[AccountTypeEnum.SPOT]!!,
                "symbol" to string(symbol),
                "size" to "$size")
        val signedUrl = buildSignedSubpath("/v1/order/openOrders", GET, params as MutableMap<String, Any>)
        val resp = get(authUrl(signedUrl))
        val obj = jsonObject(resp)
        return obj["data"].asJsonArray.map { it.asJsonObject }
                .map {
                    SpotOrder(it["id"].asString,
                            symbol,
                            orderSide(it["type"].asString),
                            it["price"].asString.toBigDecimal(),
                            it["amount"].asString.toBigDecimal(),
                            it["filled-amount"].asString.toBigDecimal(),
                            date(it["created-at"].asLong.toString()),
                            orderState(it["state"].asString),
                            orderType(it["type"].asString))
                }
    }

    override suspend fun cancelOrder(oid: String, symbol: Symbol): TransactionResult {
        val subPath = buildSignedSubpath("/v1/order/orders/$oid/submitcancel", POST)
        val resp = post(authUrl(subPath))
        val obj = jsonObject(resp)
        return if (obj.has("data")) {
            // successfully submitted
            TransactionResult(obj["data"].asString)
        } else {
            // error
            TransactionResult("", false, when (obj["order-state"].asInt) {
                -1 -> "order was already closed in the long past (order state = canceled, partial-canceled, filled, partial-filled)"
                5 -> "partial-canceled"
                6 -> "filled"
                7 -> "canceled"
                10 -> "cancelling"
                else -> error("Unknown order-state")
            })
        }
    }

    override suspend fun getFee(symbol: Symbol): SpotTradingFee {
        val params = mutableMapOf("symbols" to string(symbol))
        val signedSubPath = buildSignedSubpath("/v1/fee/fee-rate/get", GET, params as MutableMap<String, Any>)
        val resp = get(authUrl(signedSubPath))
        val data = jsonObject(resp)["data"].asJsonArray[0].asJsonObject
        return SpotTradingFee(symbol,
                data["maker-fee"].asString.toBigDecimal(),
                data["taker-fee"].asString.toBigDecimal())
    }

    private fun orderDateString(date: Date): String {
        val FORMAT = SimpleDateFormat("yyyy-MM-dd")
        return FORMAT.format(date)
    }

    /**
     * 查询所有历史订单
     *
     * 从可供查询的日期范围来看，似乎是一个挺鸡肋的功能
     *
     * @param symbol Symbol
     * @param start Date 最多为今天前180天
     * @param end Date 与start组成查询窗口，窗口最大长度为2天
     * @param state OrderStateEnum?
     * @param size Int 查询记录个数
     * @return List<SpotOrder>
     */
    override suspend fun getOrders(symbol: Symbol, start: Date, end: Date, state: OrderStateEnum?, size: Int): List<SpotOrder> {
//        state ?: error("State for getOrders can't be null.")
        val params = mutableMapOf(
                "symbol" to string(symbol),
                "start-date" to orderDateString(start),
                "end-date" to orderDateString(end),
                "size" to "$size"
        )
        if (state == null) {
            params["states"] = OrderStateEnum.values().map { string(it) }.joinToString(",")
        } else {
            params["states"] = string(state)
        }
        val subPath = buildSignedSubpath("/v1/order/orders", GET, params as MutableMap<String, Any>)
        val resp = get(authUrl(subPath))
        val obj = jsonObject(resp)
        return obj["data"].asJsonArray.map { it.asJsonObject }
                .map {
                    SpotOrder(it["id"].asString,
                            symbol,
                            orderSide(it["type"].asString),
                            it["price"].asString.toBigDecimal(),
                            it["amount"].asString.toBigDecimal(),
                            it["field-amount"].asString.toBigDecimal(),
                            date(it["created-at"].asLong.toString()),
                            orderState(it["state"].asString),
                            orderType(it["type"].asString))
                }
    }

    suspend fun getAccountIds(): Map<AccountTypeEnum, String> {
        val resp = signedGet(authUrl("/v1/account/accounts"))
        val obj = jsonObject(resp)
        val map = HashMap<AccountTypeEnum, String>()
        obj["data"].asJsonArray.map { it.asJsonObject }
                .forEach {
                    val id = it["id"].asLong.toString()
                    when (it["type"].asString) {
                        "spot" -> map[AccountTypeEnum.SPOT] = id
                        "margin" -> map[AccountTypeEnum.MARGIN] = id
                        "super-margin" -> map[AccountTypeEnum.SUPERMARGIN] = id
                        else -> {
                            //忽略OTC、点卡账户
                        }
                    }
                }
        return map
    }

    override suspend fun transferToMargin(currency: Currency, amount: BigDecimal, symbol: Symbol): TransactionResult {
        val params = mutableMapOf<String, Any>("currency" to string(currency),
                "amount" to amount.toString())
        var resp: HttpResponse<Buffer>
        if (symbol != CROSS) {
            //逐仓杠杆账户
            params["symbol"] = string(symbol)
            val signedSubpath = buildSignedSubpath("/v1/dw/transfer-in/margin", POST)
            resp = post(authUrl(signedSubpath), HttpMediaTypeEnum.JSON, params)
        } else {
            //全仓杠杆账户
            val signedSubpath = buildSignedSubpath("/v1/cross-margin/transfer-in", POST)
            resp = post(authUrl(signedSubpath), HttpMediaTypeEnum.JSON, params)
        }
        val obj = jsonObject(resp)
        return TransactionResult(obj["data"].asLong.toString(),
                obj["status"].asString == "ok")
    }

    override suspend fun transferToFuture(currency: Currency, amount: BigDecimal, symbol: Symbol): TransactionResult {
        val params = mutableMapOf<String, Any>("currency" to string(currency),
                "amount" to amount,
                "type" to "pro-to-futures")
        val resp = signedJsonPost(authUrl("https://api.huobi.pro/v1/futures/transfer"), params)
        val obj = jsonObject(resp)
        return TransactionResult(obj["data"].asLong.toString(),
                obj["status"].asString == "ok")
    }

    override suspend fun getBalance(): Map<Currency, SpotBalance> {
        val accountId = metaInfo.accountIdMap[AccountTypeEnum.SPOT]
        val resp = signedGet(authUrl("/v1/account/accounts/${accountId}/balance"))
        val data = jsonObject(resp)["data"].asJsonObject
        val map = HashMap<Currency, SpotBalance>()
        data["list"].asJsonArray.map { it.asJsonObject }
                .forEach {
                    val c = currency(it["currency"].asString)
                    var b = map[c]
                    if (b == null) {
                        b = SpotBalance(c)
                        map[c] = b
                    }
                    if (it["type"].asString == "trade") {
                        b.free = size(it["balance"], c)
                    } else {
                        b.frozen = size(it["balance"], c)
                    }
                }
        return map.filter {
            metaInfo.currencyMetaInfo[it.key]?.validateSizeIncrement(it.value.free) ?: false ||
                    metaInfo.currencyMetaInfo[it.key]?.validateSizeIncrement(it.value.frozen) ?: false
        }
    }

    override suspend fun subscribeBalance(symbol: Symbol): Subscription<SpotBalance> {
        return super.subscribeBalance(symbol)
    }

    override suspend fun subscribeOrder(symbol: Symbol): Subscription<SpotOrder> {
        return super.subscribeOrder(symbol)
    }
}