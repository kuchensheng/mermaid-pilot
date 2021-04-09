package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.generateTraceId
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinTask

/**
 * description: 线程处理器
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2310:37
 * @version 1.0
 */
class ThreadHandler : IHandler {
    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        //获取当前traceId
        var span : Span? = getCurrentSpan()?:Span(generateTraceId())
        return createEnterSpan(span).apply {
            this.className = className
            this.methodName = method.name
            this.type = HandlerType.THREAD.name
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        TODO("Not yet implemented")
    }
}