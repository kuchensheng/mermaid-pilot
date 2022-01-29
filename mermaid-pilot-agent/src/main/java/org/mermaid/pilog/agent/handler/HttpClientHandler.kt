package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.collectParameters
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.*
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
        this.parameterInfo = collectParameters(method,args)
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        getCurrentSpanAndRemove(thrown)
    }
}