package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.generateSpanId
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.springframework.http.HttpRequest
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2217:00
 * @version 1.0
 */
class HttpClientHandler : IHandler {
    override fun before(className: String?, method: Method, args: Array<*>?): Span  = createEnterSpan("0",getTraceId()).apply {
        this.parentId = "0"
        this.appName = getAppName()
        this.className = className
        this.methodName = method.name
        this.requestMethod = requestMethod
        this.startTime = LocalDateTime.now()
        this.requestUri = requestUri
        this.type = HandlerType.HTTPCLIENT.name
    }.also { span ->
        args?.forEach {
            if (it is HttpRequest) {
                span.requestMethod = it.methodValue
                if (!it.headers.contains(HEADER_TRACE_ID)) {
                    it.headers.set(HEADER_TRACE_ID,span.traceId)
                    span.requestUri = it.uri.toString()
                }
            }
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        getCurrentSpanAndRemove()?.let { it.endTime = LocalDateTime.now()
            it.methodName = method.name
            it.costTime = Duration.between(it.startTime,it.endTime).toMillis()
            produce(it) }
    }
}