package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.generateTraceId
import org.mermaid.pilog.agent.common.setTraceId
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.model.createEnterSpan
import org.slf4j.LoggerFactory
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.lang.reflect.Method
import java.time.LocalDateTime

/**
 * description: SpringBoot / Spring 的Servlet拦截处理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:05
 * @version 1.0
 */
class ServletHandler : IHandler {
    private val logger = LoggerFactory.getLogger(ServletHandler::class.java)
    private val HEADER_TRACE_ID = "t-header-trace-id"
    private val HEADER_SPAN_ID = "t-header-span-id"
    private fun getAppName() : String? = object : EnvironmentAware {
        var appName:String? = null
        override fun setEnvironment(p0: Environment) {
            appName = p0.getProperty("spring.application.name")
        }
    }.run { appName }

    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        val requestAttribute = RequestContextHolder.getRequestAttributes()
        val request = requestAttribute?.let { (it as ServletRequestAttributes).request }
        val traceId = request?.getHeader(HEADER_TRACE_ID)?: generateTraceId()
        val rpcId =  request?.getHeader(HEADER_SPAN_ID)
        setTraceId(traceId)
        val parameterInfo = hashMapOf<String,Any?>()
        if (!args.isNullOrEmpty()) {
            method.parameters?.indices?.forEach {
                parameterInfo[method.parameters[it].name] = args[it]
            }
        }

        return createEnterSpan(rpcId).apply {
            this.type = HandlerType.REQ.name
            this.className = className
            this.methodName = method.name
            this.startTime = LocalDateTime.now()
            this.parameterInfo = parameterInfo
            this.requestUri = request?.requestURI
            this.requestMethod = request?.method
            this.appName = getAppName()
            this.parentId = rpcId
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable) {
        //todo 如果thrown不为空，则必须记录，否则可通过配置进行记录过滤
        val requestAttribute = RequestContextHolder.getRequestAttributes()
        val response = requestAttribute?.let { (it as ServletRequestAttributes).response }
        //todo 是否记录
        org.mermaid.pilog.agent.model.getCurrentSpan()?.apply {
            response?.setHeader(HEADER_TRACE_ID,traceId)
            response?.setHeader(HEADER_SPAN_ID,spanId)
        }.run {
            //todo 收集span信息并上传到服务端
            println(toString())
        }
    }
}