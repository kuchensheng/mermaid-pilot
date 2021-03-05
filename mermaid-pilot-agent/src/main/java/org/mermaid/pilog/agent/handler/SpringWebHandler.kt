package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.*
import org.mermaid.pilog.agent.model.*
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.lang.reflect.Method

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
        val request = RequestContextHolder.getRequestAttributes().let { (it as ServletRequestAttributes).request }
        request.headerNames.iterator().forEach {
            println("SpringWebHandler 请求头：$it,值：${request.getHeader(it)}")
        }
        request.getHeader(HEADER_TRACE_ID)?.run {
            setTraceId(this)
            addHeader(request, HEADER_TRACE_ID,this)
        }.also { println("从请求头中获取traceId=$it") }

        return createEnterSpan(getCurrentSpan()).apply {
            this.className = className
            this.methodName = method.name
            this.parameterInfo = collectParameters(method,args)
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