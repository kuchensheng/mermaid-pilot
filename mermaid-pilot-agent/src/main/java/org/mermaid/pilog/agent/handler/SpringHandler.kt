package org.mermaid.pilog.agent.handler

import net.sf.json.JSONObject
import org.mermaid.pilog.agent.common.collectParameters
import org.mermaid.pilog.agent.common.generateTraceId
import org.mermaid.pilog.agent.common.getAndSetTraceId
import org.mermaid.pilog.agent.common.setTraceId
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.*
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.lang.reflect.Method

/**
 * description: 这对方法的拦截，目前是针对了@Service注解的类的public方法进行的拦截
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2218:09
 * @version 1.0
 */
class SpringHandler : IHandler {
    private val logger = LoggerFactory.getLogger(SpringHandler::class.java)
    override fun before(className: String?, method: Method, args: Array<*>?): Span = createEnterSpan(getCurrentSpan()).apply {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val uri =  request.requestURI.toString()
        val remoteIp = getOrininalIp(request)
        val remoteAppName = getRemoteAppName(request)
        //获取上一个span的spanId,这个Id是本次span的parentId
        var traceId = request.getHeader(HEADER_TRACE_ID)
        if (traceId.isNullOrBlank()) {
            traceId = generateTraceId()
        }else {
            setTraceId(traceId)
        }

        return createEnterSpan(getCurrentSpan()).apply {
            this.traceId = traceId
            this.originIp = remoteIp
            this.originAppName = remoteAppName
            this.type = HandlerType.SPRINGWEB.name
            this.className = className
//            this.parameterInfo = getParameterInfo(request)
            this.requestUri = uri
            this.requestMethod = request.method
            this.methodName = request.method
        }.also {
            logger.info("span信息:${JSONObject.fromObject(it)}")
        }
    }

    override fun after(className: String?, method: Method, array: Array<*>?, result: Any?, thrown: Throwable?) {
        getCurrentSpanAndRemove(thrown,true)
    }
}

