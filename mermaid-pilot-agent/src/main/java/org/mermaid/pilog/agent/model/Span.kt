package org.mermaid.pilog.agent.model

import com.alibaba.fastjson.JSONObject
import org.mermaid.pilog.agent.common.generateSpanId
import org.mermaid.pilog.agent.common.generateTraceId
import java.time.LocalDateTime
import java.util.*

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
    var startTime: LocalDateTime = LocalDateTime.now()
    var endTime: LocalDateTime? = null
    var parameterInfo: Map<String,Any?>? = null
    var result:Any? = null
    var className: String? = null
    var methodName: String = ""
    var costTime : Long = 0
    var requestUri:String? = null
    var requestMethod: String? = null
    var appName: String? = null
    var throwable: Throwable? = null

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

fun createEnterSpan(rpcId: String?, traceId: String?) : Span  = Span(traceId?: generateTraceId()).apply {
    spanId = generateSpanId(rpcId)
    startTime = LocalDateTime.now()
    val stack = localSpan.get()
    if (stack.isNullOrEmpty()) {
        localSpan.set(Stack())
    }
    localSpan.get().push(this)
}

fun getCurrentSpan() : Span? = localSpan.get()?.let { it.peek() }