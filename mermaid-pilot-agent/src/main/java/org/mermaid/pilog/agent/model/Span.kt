package org.mermaid.pilog.agent.model

import net.sf.json.JSONObject
import org.mermaid.pilog.agent.common.config
import org.mermaid.pilog.agent.common.generateSpanId
import org.mermaid.pilog.agent.common.getAndSetTraceId
import org.mermaid.pilog.agent.common.produce
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.getOrSet

/**
 * description: 链路跟踪模型
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1910:58
 * @version 1.0
 */
class Span : LogModel {
    var originAppName: String = ""
    var originIp: String = ""
    var type: String? = ""
    var traceId : String
    var spanId: String = ""
    var parentId:String = ROOT_SPAN_ID
    var startTime: LocalDateTime = LocalDateTime.now()
    var endTime: LocalDateTime? = null
    var parameterInfo: MutableMap<String,Any?> = mutableMapOf()
    var result:Any? = null
    var className: String? = null
    var methodName: String = ""
    var costTime : Long = 0
    var requestUri:String? = null
    var requestMethod: String? = null
    var throwable: Throwable? = null
    val hostName : String = getHostName()

    @JvmOverloads
    constructor(traceId: String) {
        this.traceId = traceId
    }

    override fun toString(): String  = """
        {"traceId":"$traceId","spanId":"$spanId","parentId":"$parentId","type":"$type",
        "startTime":"$startTime","endTime":"$endTime","className":"$className","methodName":"$methodName",
        "originalIp":"$originIp","originalApp":"$originAppName","costTime":",$costTime,",
        "requestUri":"$requestUri","requestMethod":"$requestMethod","hostName":"$hostName",
        "parameterInfo":"$parameterInfo","throwable":"$throwable"}""".trimIndent().trim()
}

val localSpan = ThreadLocal<Stack<Span>>()
private val logger: Logger = LoggerFactory.getLogger(Span::class.java)
/**
 * 获取当前服务的IP地址
 */
fun getHostName(): String = Inet4Address.getLocalHost().hostAddress
const val ROOT_SPAN_ID = "0"
val lock = ReentrantLock()

/**
 * 创建span信息
 */
fun createEnterSpan(currentSpan: Span?) : Span {
    lock.lock()
    return try {
        Span(currentSpan?.traceId ?: getAndSetTraceId()).apply {
            this.parentId = currentSpan?.spanId ?: ROOT_SPAN_ID
            this.spanId = generateSpanId(this.traceId)
            this.startTime = LocalDateTime.now()
            localSpan.getOrSet { Stack() }.push(this)
        }
    } finally {
        lock.unlock()
    }
}

/**
 * 获取栈顶的span信息
 */
fun getCurrentSpan() : Span?  = localSpan.get()?.let { if (!it.isNullOrEmpty()) it.peek() else null }

fun getCurrentSpanAndRemove(throwable: Throwable?,isTrace :Boolean? = false) = getCurrentSpan()?.run {
//    logger.info("获取当前span信息,${JSONObject.fromObject(this)}")
    this.endTime = LocalDateTime.now()
    this.costTime = Duration.between(this.startTime,this.endTime).toMillis()
    this.throwable = throwable
    this.content = buildLog(this)
    produce(this)
    localSpan.get().remove(this)
}

//组装trace信息
fun buildLog(span : Span) : String = mutableListOf("0",
        span.startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli().toString(),
        span.traceId,
        span.spanId,
        "1",
        span.type,
        "<${span.methodName}>${span.requestUri}",
        config.appName,
        getHostName(),
        span.originIp,
        span.throwable?.run { "0" } ?: "1",
        span.result?.toString()?.length?.toString()?:"0",
        span.costTime.toString(),
        span.throwable?.message?:"").joinToString("|")