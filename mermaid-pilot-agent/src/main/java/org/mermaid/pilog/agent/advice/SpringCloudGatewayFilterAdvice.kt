package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import org.mermaid.pilog.agent.common.getParameterInfo
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.common.setTraceId
import org.mermaid.pilog.agent.handler.*
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.mermaid.pilog.agent.model.getHostName
import org.mermaid.pilog.agent.plugin.factory.logger
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import java.lang.reflect.Method

/**
 * description: Springcloud gateway global filter 通知
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2415:39
 * @version 1.0
 */
object SpringCloudGatewayFilterAdvice {
    @JvmStatic
    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") className: String,
              @Advice.Origin method: Method,
              @Advice.Argument(value = 0, readOnly = true, typing = Assigner.Typing.DYNAMIC) exchange: ServerWebExchange?,
              @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) chain : Any?) {
        exchange?.run {
            val remoteIp = getOriginalIp(this)
            val remoteAppName = getRemoteAppName(this)
            this.request.apply {
                headers?.run {
                    getFirst(HEADER_TRACE_ID)?.let { t -> setTraceId(t) } ?: getTraceId().also { traceId ->
                        add(HEADER_TRACE_ID,traceId)
                    }
                    add(HEADER_REMOTE_IP, getHostName())
                    add(HEADER_REMOTE_APP, getAppName())
                }
            }
            logger.info("请求头设置完毕，开始创建span信息")
            createEnterSpan(getCurrentSpan()).let {
                it.className = className
                it.methodName = method.name
                it.requestUri = exchange?.let { this.request.uri.path }
                it.requestMethod = exchange?.let { this.request.methodValue }
                it.parameterInfo = getParameterInfo(this.request)
                it.type = "GATEWAY"
                it.originIp = remoteIp
                it.originAppName = remoteAppName
                produce(it)
            }
            logger.info("创建span信息创建完毕")
        }
    }

    @JvmStatic
    @Advice.OnMethodExit(onThrowable = Throwable::class)
    fun exit(@Advice.Thrown throwable: Throwable?) = getCurrentSpanAndRemove(throwable)
}