package org.mermaid.pilog.agent.model

import org.mermaid.pilog.agent.common.generateSpanId
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.plugin.factory.logger
import java.net.Inet4Address
import java.time.Duration
import java.time.LocalDateTime
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
class Span {
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
    var appName: String? = getAppName()
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
        "requestUri":"$requestUri","requestMethod":"$requestMethod","appName":"$appName","hostName":"$hostName",
        "parameterInfo":"$parameterInfo","throwable":"$throwable"}""".trimIndent().trim()
}

val localSpan = ThreadLocal<Stack<Span>>()

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
        Span(getTraceId()).apply {
            this.spanId = generateSpanId((currentSpan?.spanId?: ROOT_SPAN_ID).also { this.parentId = it })
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

fun getCurrentSpanAndRemove(throwable: Throwable?) = localSpan.get()?.let { if (!it.isNullOrEmpty()) it.pop() else null }?.apply {
    this.endTime = LocalDateTime.now()
    this.costTime = Duration.between(this.startTime,this.endTime).toMillis()
    this.throwable = throwable
    logger.apply { this.level = java.util.logging.Level.OFF }.info("取出span：${toString()}")
    produce(this)
}