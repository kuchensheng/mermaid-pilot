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
    private val HEADER_TRACE_ID = "t-header-trace-id"
    private val HEADER_SPAN_ID = "t-header-span-id"
    private fun getAppName() : String? = object : EnvironmentAware {
        var appName:String? = null
        override fun setEnvironment(p0: Environment) {
            appName = p0.getProperty("spring.application.name")
        }
    }.run { appName }
    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        val request = RequestContextHolder.getRequestAttributes()?.let { (it as ServletRequestAttributes).request }
        request?.getHeader(HEADER_TRACE_ID)?: generateTraceId().run { setTraceId(this) }
        val rpcId =  request?.getHeader(HEADER_SPAN_ID)
        val parameterInfo = hashMapOf<String,Any?>()
        if (!args.isNullOrEmpty()) {
            method.parameters?.indices?.forEach {
                parameterInfo[method.parameters[it].name] = args[it]
            }
        }
        return createEnterSpan(rpcId, getTraceId()).apply {
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

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        //todo 如果thrown不为空，则必须记录，否则可通过配置进行记录过滤
        val response = RequestContextHolder.getRequestAttributes()?.let { (it as ServletRequestAttributes).response }
        //todo 是否记录
        logger.info("springweb执行完毕，执行方法:${method.name}")
        getCurrentSpanAndRemove()?.let {
            if (HandlerType.SERVLET.name == it.type) localSpan.remove()
            it.type = HandlerType.SPRINGWEB.name
            it.methodName = method.name
            it.endTime = LocalDateTime.now()
            it.costTime = Duration.between(it.startTime,it.endTime).toMillis()
            produce(it)
//            response?.setHeader(HEADER_TRACE_ID,traceId)
//            response?.setHeader(HEADER_SPAN_ID,spanId)
        }
    }
}