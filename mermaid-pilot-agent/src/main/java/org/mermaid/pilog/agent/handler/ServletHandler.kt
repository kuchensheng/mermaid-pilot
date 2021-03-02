package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.common.*
import org.mermaid.pilog.agent.config.LocalAppConfig
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.*
import org.mermaid.pilog.agent.plugin.factory.logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
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
const val HEADER_REMOTE_IP = "t-remote-ip"
const val HEADER_REMOTE_APP = "t-remote-app"
val parameterNames = hashSetOf("sec-fetch-mode","sec-fetch-site","accept-language","sec-fetch-user","cache-control","user-agent","sec-fetch-dest","host","accept-encoding")
class ServletHandler : IHandler {
    private val logger = LoggerFactory.getLogger(ServletHandler::class.java)

    override fun before(className: String?, method: Method, args: Array<*>?): Span {
        val request = args?.get(0) as HttpServletRequest
        val uri =  request.requestURI.toString()
        val remoteIp = getOrininalIp(request)
        val remoteAppName = getRemoteAppName(request)
        //获取上一个span的spanId,这个Id是本次span的parentId
        request.getHeader(HEADER_TRACE_ID) ?: getTraceId().also {
            addHeader(request, HEADER_TRACE_ID,it)
            addHeader(request, HEADER_REMOTE_IP, getHostName())
            addHeader(request, HEADER_REMOTE_APP, getAppName())
        }

        return createEnterSpan(getCurrentSpan()).apply {
            this.originIp = remoteIp
            this.originAppName = remoteAppName
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

fun addHeader(request: HttpServletRequest?, name: String, value: String) = request?.let { object : HttpServletRequestWrapper(request) {
    private val customHeaders = hashMapOf<String,String>()
    fun putHeader(name:String,value: String) {customHeaders[name] = value}
    override fun getHeader(name: String?): String = customHeaders[name]?:request.getHeader(name)
    override fun getHeaderNames(): Enumeration<String> = Collections.enumeration(hashSetOf<String>().apply {
        request.headerNames.iterator().forEach { add(it) }
    })
}.run {
    putHeader(name,value)
} }

fun getOrininalIp(request: HttpServletRequest) : String {
    request.getHeader("t-remote-ip")?.run { return this }
    var ip = request.getHeader("X-Forwarded-For")
    if (!(null == ip || "unknown".equals(ip,true))) {
        //多次反向代理后有多个IP值，第一个是真实IP
        return if (ip.contains(",")) ip.split(",").first() else ip
    }
    ip = request.getHeader("X-Real-IP")
    if (!(null == ip || "unknown".equals(ip,true))) return ip
    return request.remoteAddr
}

@Autowired
internal var env : Environment? = null

fun getAppName() : String  = env?.getProperty("spring.application.name")?.also { logger.info("获取到的appName：$it") }?: System.getProperty("application.name","")

fun getRemoteAppName(request: HttpServletRequest): String = request.getHeader(HEADER_REMOTE_APP)

