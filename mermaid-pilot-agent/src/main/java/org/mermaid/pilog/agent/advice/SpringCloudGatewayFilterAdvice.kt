package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import org.mermaid.pilog.agent.common.getParameterInfo
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
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
              @Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) exchange: Any?,
              @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) chain : Any?) {
        exchange?.let {
            if (exchange is ServerWebExchange) {
                createEnterSpan(getCurrentSpan()).apply {
                    this.className = className
                    this.methodName = method.name
                    this.requestUri = exchange?.let { it.request.uri.path }
                    this.requestMethod = exchange?.let { it.request.methodValue }
                    this.parameterInfo = getParameterInfo(exchange?.request)
                    this.type = "GATEWAY"
                }.run {
                    produce(this)
                }
            }
        }


    }

    @JvmStatic
    @Advice.OnMethodExit(onThrowable = Throwable::class)
    fun exit(@Advice.Thrown throwable: Throwable, ) = getCurrentSpanAndRemove(throwable)
}