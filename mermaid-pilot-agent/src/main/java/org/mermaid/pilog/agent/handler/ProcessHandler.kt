package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime

/**
 * description: 这对方法的拦截，目前是针对了@Service注解的类的public方法进行的拦截
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2218:09
 * @version 1.0
 */
class ProcessHandler : IHandler {
    override fun before(className: String?, method: Method, args: Array<*>?): Span = createEnterSpan(getCurrentSpan()?.spanId, getTraceId()).apply {
        this.startTime = LocalDateTime.now()
        this.type = HandlerType.PROCESS.name
        this.methodName = method.name
        this.parameterInfo = collectParameters(method,args)
        this.appName = getAppName()
        this.className = className
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        getCurrentSpanAndRemove()?.let {
            it.methodName = method.name
            it.endTime = LocalDateTime.now()
            it.costTime = Duration.between(it.startTime,it.endTime).toMillis()
            produce(it)
        }
    }
}

fun collectParameters(method: Method,args: Array<*>?) : Map<String,*>?  = hashMapOf<String,Any?>().apply {
    method.parameters?.indices?.forEach {
        put(method.parameters[it].name, args?.get(it))
    }
}