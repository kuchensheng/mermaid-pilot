package org.mermaid.pilog.agent.advice

import cn.hutool.http.HttpRequest
import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bind.annotation.Super
import org.mermaid.pilog.agent.common.getParameterInfo
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_APP
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_IP
import org.mermaid.pilog.agent.handler.HEADER_TRACE_ID
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.mermaid.pilog.agent.model.getHostName
import java.lang.reflect.Method
import java.net.URL

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/413:56
 * @version 1.0
 */
object HutoolClientAdvice {
    @Advice.OnMethodEnter
    @JvmStatic
    fun onMethodExecution(@Advice.This(optional = true) instance : Any,
                          @Advice.Origin("#m") method: Method){
        createEnterSpan(getCurrentSpan()).apply {
            instance as HttpRequest
            this.className = instance::class.java.name
            this.requestMethod = instance.method.name
            this.requestUri = URL(instance.url).toURI().path
            this.parameterInfo = getParameterInfo(instance)
            instance.header(HEADER_TRACE_ID, getTraceId())
                    .header(HEADER_REMOTE_IP, getHostName())
                    .header(HEADER_REMOTE_APP, getAppName())
            println("设置请求头")
            this.methodName = method.name
            this.type = HandlerType.HTTPCLIENT.name

        }

    }

    @Advice.OnMethodExit(onThrowable = Throwable::class)
    @JvmStatic
    fun onMethodExit(@Advice.Thrown throwable: Throwable?) = getCurrentSpanAndRemove(throwable)
}