package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.generateSpanId
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
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

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
    private val parameterNames = hashSetOf("sec-fetch-mode","sec-fetch-site","accept-language","sec-fetch-user","cache-control","user-agent","sec-fetch-dest","host","accept-encoding")
    private fun getAppName() : String? = object : EnvironmentAware {
        var appName:String? = null
        override fun setEnvironment(p0: Environment) {
            appName = p0.getProperty("spring.application.name")
        }
    }.run { appName }

    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        val request = args?.get(0) as HttpServletRequest
        val uri =  request.requestURI.toString()
        //获取上一个span的spanId,这个Id是本次span的parentId
        val parentId = request.getHeader(HEADER_SPAN_ID)?:"0"
        var traceId : String? = request.getHeader(HEADER_TRACE_ID)
        if (traceId.isNullOrEmpty()) {
            generateTraceId().apply { setTraceId(this) }.also {
                object : HttpServletRequestWrapper(request) {
                    private val customHeaders = hashMapOf<String,String>()
                    fun putHeader(name:String,value: String) {customHeaders[name] = value}
                    override fun getHeader(name: String?): String = customHeaders[name]?:request.getHeader(name)
                    override fun getHeaderNames(): Enumeration<String> = Collections.enumeration(hashSetOf<String>().apply {
                        request.headerNames.iterator().forEach { add(it) }
                    })
                }.run {
                    putHeader(HEADER_TRACE_ID,it)
                    putHeader(HEADER_SPAN_ID,parentId)
                }
            }
        }
        return createEnterSpan(parentId).apply {
            this.type = HandlerType.REQ.name
            this.className = className
            this.methodName = method.name
            this.startTime = LocalDateTime.now()
            this.parameterInfo = getParameterInfo(request)
            this.requestUri = uri
            this.requestMethod = request?.method
            this.appName = getAppName()
            this.parentId = parentId
            this.spanId = generateSpanId(parentId)
        }
    }

    private fun getParameterInfo(request: HttpServletRequest): Map<String, Any?>? {
        val parameterMap = hashMapOf<String,Any?>()
        //读取url参数信息
        request.parameterNames?.iterator()?.forEach {
            parameterMap[it] = request.getParameter(it)
        }
        request.headerNames?.iterator()?.forEach {
            if (!parameterNames.contains(it)) parameterMap[it] = request.getHeader(it)
        }
        request.reader.readText()?.let { parameterMap["requestBody"] = it }
        return parameterMap
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        //todo 如果thrown不为空，则必须记录，否则可通过配置进行记录过滤
//        val requestAttribute = RequestContextHolder.getRequestAttributes()
//        val response = requestAttribute?.let { (it as ServletRequestAttributes).response }
        //todo 是否记录
        org.mermaid.pilog.agent.model.getCurrentSpan()?.apply {
//            response?.setHeader(HEADER_TRACE_ID,traceId)
//            response?.setHeader(HEADER_SPAN_ID,spanId)
        }.run {
            //todo 收集span信息并上传到服务端
            println("className:${className},methodName:${method.name},执行完毕，进行信息收集：${toString()}")
        }
    }
}