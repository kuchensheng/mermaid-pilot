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
 * description: http客户端处理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2217:00
 * @version 1.0
 */
class HttpClientHandler : IHandler {
    override fun before(className: String?, method: Method, args: Array<*>?): Span  = createEnterSpan(getCurrentSpan()?.let { it.spanId },getTraceId()).apply {
        this.className = className
        this.methodName = method.name
        this.requestMethod = requestMethod
        this.type = HandlerType.HTTPCLIENT.name
        this.parameterInfo = collectParameters(method,args)
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
        getCurrentSpanAndRemove(thrown)
    }
}