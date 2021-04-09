package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.collectParameters
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.*
import org.springframework.http.HttpRequest
import java.lang.reflect.Method

/**
 * description: http客户端处理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2217:00
 * @version 1.0
 */
class HttpClientHandler : IHandler {
    override fun before(className: String?, method: Method, args: Array<*>?): Span  = createEnterSpan(getCurrentSpan()).apply {
        println("执行类，className = $className")
        this.className = className
        this.methodName = method.name
        this.type = HandlerType.HTTPCLIENT.name
        args?.firstOrNull { it is HttpRequest }.let {
            this.requestUri = (it as HttpRequest).uri.toString()
            this.requestMethod = it.methodValue
        }
        this.parameterInfo = collectParameters(method,args)
    }.also { span ->
        args?.forEach {
            if (it is HttpRequest) {
                span.requestMethod = it.methodValue
                if (!it.headers.contains(HEADER_TRACE_ID)) {
                    it.headers.set(HEADER_TRACE_ID,span.traceId)
                    it.headers.set(HEADER_REMOTE_IP, getHostName())
                    it.headers.set(HEADER_REMOTE_APP, getAppName())
                    span.requestUri = it.uri.toString()
                }
            }
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        getCurrentSpanAndRemove(thrown)
    }
}