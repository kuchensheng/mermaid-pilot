package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.*
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.http.HttpRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.lang.reflect.Method
import java.time.Duration
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
const val HEADER_TRACE_ID = "t-header-trace-id"
const val HEADER_SPAN_ID = "t-header-span-id"
val parameterNames = hashSetOf("sec-fetch-mode","sec-fetch-site","accept-language","sec-fetch-user","cache-control","user-agent","sec-fetch-dest","host","accept-encoding")
class ServletHandler : IHandler {
    private val logger = LoggerFactory.getLogger(ServletHandler::class.java)

    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        val request = args?.get(0) as HttpServletRequest
        val uri =  request.requestURI.toString()
        //获取上一个span的spanId,这个Id是本次span的parentId
        val parentId = (request.getHeader(HEADER_SPAN_ID)?: getCurrentSpan()?.let { it.spanId }) ?: "0"
        var traceId = request.getHeader(HEADER_TRACE_ID) ?: getTraceId().also {
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

        return createEnterSpan(parentId, traceId).apply {
            this.type = HandlerType.SERVLET.name
            this.className = className
            this.startTime = LocalDateTime.now()
            this.parameterInfo = getParameterInfo(request)
            this.requestUri = uri
            this.requestMethod = request?.method
            this.appName = getAppName()
            this.parentId = parentId
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        //todo 如果thrown不为空，则必须记录，否则可通过配置进行记录过滤
//        val requestAttribute = RequestContextHolder.getRequestAttributes()
//        val response = requestAttribute?.let { (it as ServletRequestAttributes).response }
        //todo 是否记录
        logger.info("servlet执行完毕，执行方法:${method.name}")
        getCurrentSpanAndRemove()?.let {
            it.methodName = method.name
            it.endTime = LocalDateTime.now()
            it.costTime = Duration.between(it.startTime,it.endTime).toMillis()
            produce(it)
        }
    }
}

fun getAppName() : String? = object : ApplicationContextAware {
    var appName:String? = null
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        appName = applicationContext.applicationName
    }
}.run { appName }

fun getParameterInfo(request: HttpServletRequest): Map<String, Any?>? {
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

fun getParameterInfo(request: HttpRequest?): Map<String, Any?>?  = hashMapOf<String,Any?>().apply { request?.headers?.filter { !parameterNames.contains(it) }?.run { putAll(this) } }
