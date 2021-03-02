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
import kotlin.collections.HashMap
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
    var type: String? = ""
    var traceId : String
    var spanId: String = ""
    var parentId:String = ROOT_SPAN_ID
    var seq : Int = 0
    var startTime: LocalDateTime = LocalDateTime.now()
    var endTime: LocalDateTime? = null
    var parameterInfo: MutableMap<String,Any?>? = null
    var result:Any? = null
    var className: String? = null
    var methodName: String = ""
    var costTime : Long = 0
    var requestUri:String? = null
    var requestMethod: String? = null
    var appName: String? = getAppName()
    var throwable: Throwable? = null
    val hostName : String = Inet4Address.getLocalHost().hostAddress

    @JvmOverloads
    constructor(traceId: String) {
        this.traceId = traceId
    }

    override fun toString(): String  = """{"type":"$type","traceId":"$traceId","spanId":"$spanId","parentId":"$parentId","seq":"$seq","startTime":"$startTime","endTime":"$endTime","parameterInfo":"$parameterInfo","className":"$className","methodName":"$methodName","costTime":"$costTime","requestUri":"$requestUri","requestMethod":"$requestMethod","appName":"$appName","hostName":"$hostName","throwable":"$throwable"}""".trim()
}

val localSpan = ThreadLocal<Stack<Span>>()

const val ROOT_SPAN_ID = "0"
fun createEnterSpan(rpcId: String?) : Span  = Span(getTraceId()).apply {
    spanId = generateSpanId(rpcId)
    startTime = LocalDateTime.now()
    val stack = localSpan.get()
    if (stack.isNullOrEmpty()) {
        localSpan.set(Stack())
    }
    localSpan.get().push(this)
}
val lock = ReentrantLock()
fun createEnterSpan(rpcId: String?, traceId: String?) : Span  = lock.lock().let {
    Span(getTraceId()).apply {
        this.spanId = generateSpanId(rpcId)
        rpcId?.let { parentId = it }
        startTime = LocalDateTime.now()
        localSpan.getOrSet { Stack() }.push(this)
    }.apply { lock.unlock() }
}

fun createEnterSpan(currentSpan: Span?) = lock.lock().let {
    Span(getTraceId()).apply {
        this.spanId = generateSpanId(currentSpan?.spanId?.also { this.parentId = it })
        this.startTime = LocalDateTime.now()
        currentSpan?.let { this.seq += (it.seq?: 1) }
        localSpan.getOrSet { Stack() }.push(this)
    }.also {
        lock.unlock() }
}
fun getCurrentSpan() : Span? = localSpan.get()?.let { if (!it.isNullOrEmpty()) it.peek() else null }

fun getCurrentSpanAndRemove(throwable: Throwable?) = localSpan.get()?.let { if (!it.isNullOrEmpty()) it.pop() else null }?.apply {
    this.endTime = LocalDateTime.now()
    this.costTime = Duration.between(this.startTime,this.endTime).toMillis()
    this.throwable = throwable
    logger.info("取出span：${toString()}")
    produce(this)
}