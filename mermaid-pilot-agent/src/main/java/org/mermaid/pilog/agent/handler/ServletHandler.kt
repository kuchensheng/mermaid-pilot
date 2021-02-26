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
import org.springframework.http.HttpRequest
import org.springframework.web.server.ServerWebExchange
import java.lang.reflect.Method
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

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
        val remoteIp = getOrininalIp(request)
        //获取上一个span的spanId,这个Id是本次span的parentId
        request.getHeader(HEADER_TRACE_ID) ?: getTraceId().also { addHeader(request,it)}

        return createEnterSpan(getCurrentSpan()).apply {
            this.type = HandlerType.SERVLET.name
            this.className = className
            this.parameterInfo = getParameterInfo(request)
            this.requestUri = uri
            this.requestMethod = request?.method
            this.methodName = method.name
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        //todo 如果thrown不为空，则必须记录，否则可通过配置进行记录过滤
//        val requestAttribute = RequestContextHolder.getRequestAttributes()
//        val response = requestAttribute?.let { (it as ServletRequestAttributes).response }
        //todo 是否记录
        getCurrentSpanAndRemove(thrown)
    }
}

fun addHeader(request: HttpServletRequest?, traceId: String) = request?.let { object : HttpServletRequestWrapper(request) {
    private val customHeaders = hashMapOf<String,String>()
    fun putHeader(name:String,value: String) {customHeaders[name] = value}
    override fun getHeader(name: String?): String = customHeaders[name]?:request.getHeader(name)
    override fun getHeaderNames(): Enumeration<String> = Collections.enumeration(hashSetOf<String>().apply {
        request.headerNames.iterator().forEach { add(it) }
    })
}.run {
    putHeader(HEADER_TRACE_ID,traceId)
} }

fun getOrininalIp(request: HttpServletRequest) : String {
    var ip = request.getHeader("X-Forwarded-For")
    if (!(null == ip || "unknown".equals(ip,true))) {
        //多次反向代理后有多个IP值，第一个是真实IP
        return if (ip.contains(",")) ip.split(",").first() else ip
    }
    ip = request.getHeader("X-Real-IP")
    if (!(null == ip || "unknown".equals(ip,true))) return ip
    return request.remoteAddr
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
