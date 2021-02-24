package org.mermaid.pilog.agent.model

import com.alibaba.fastjson.JSONObject
import org.mermaid.pilog.agent.common.generateSpanId
import org.mermaid.pilog.agent.common.generateTraceId
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.handler.getAppName
import java.net.Inet4Address
import java.net.InetAddress
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
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
    var type: String? = ""
    var traceId : String
    var spanId: String = ""
    var parentId:String? = null
    var seq : Int = 0
    var startTime: LocalDateTime = LocalDateTime.now()
    var endTime: LocalDateTime? = null
    var parameterInfo: Map<String,Any?>? = null
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

    override fun toString(): String {
        return JSONObject.toJSONString(this)
    }
}

val localSpan = ThreadLocal<Stack<Span>>()

val ROOT_SPAN_ID = "0"
fun createEnterSpan(rpcId: String?) : Span  = Span(generateTraceId()).apply {
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
    Span(traceId?: generateTraceId()).apply {
        this.spanId = generateSpanId(rpcId)
        this.seq += 1
        rpcId?.let { parentId = it }
        startTime = LocalDateTime.now()
        localSpan.getOrSet { Stack() }.push(this)
    }.apply { lock.unlock() }
}

fun getCurrentSpan() : Span? = localSpan.get()?.let { if (!it.isNullOrEmpty()) it.peek() else null }

fun getCurrentSpanAndRemove(throwable: Throwable?) = localSpan.get()?.let { if (!it.isNullOrEmpty()) it.pop() else null }?.apply {
    this.endTime = LocalDateTime.now()
    this.costTime = Duration.between(this.startTime,this.endTime).toMillis()
    this.throwable = throwable
    produce(this)
}