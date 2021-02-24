package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.*
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.*
import org.mermaid.pilog.agent.plugin.factory.logger
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2011:30
 * @version 1.0
 */
class SpringWebHandler : IHandler {
    private fun getAppName() : String? = object : EnvironmentAware {
        var appName:String? = null
        override fun setEnvironment(p0: Environment) {
            appName = p0.getProperty("spring.application.name")
        }
    }.run { appName }
    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        val request = RequestContextHolder.getRequestAttributes()?.let { (it as ServletRequestAttributes).request }
        var traceId = request?.getHeader(HEADER_TRACE_ID)?: getTraceId()
        val rpcId =  (request?.getHeader(HEADER_SPAN_ID) ?: getCurrentSpan()?.let { it.spanId }) ?: "0"
        val parameterInfo = hashMapOf<String,Any?>()
        if (!args.isNullOrEmpty()) {
            method.parameters?.indices?.forEach {
                parameterInfo[method.parameters[it].name] = args[it]
            }
        }
        return createEnterSpan(rpcId, traceId).apply {
            this.className = className
            this.methodName = method.name
            this.parameterInfo = parameterInfo
            this.requestUri = request?.requestURI
            this.requestMethod = request?.method
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        //todo 如果thrown不为空，则必须记录，否则可通过配置进行记录过滤
        val response = RequestContextHolder.getRequestAttributes()?.let { (it as ServletRequestAttributes).response }
        //todo 是否记录
        getCurrentSpanAndRemove(thrown)
    }
}