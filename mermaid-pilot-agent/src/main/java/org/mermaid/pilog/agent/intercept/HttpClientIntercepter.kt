package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bind.annotation.*
import org.mermaid.pilog.agent.common.collectParameters
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
import java.util.concurrent.Callable

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/315:23
 * @version 1.0
 */
open class HttpClientIntercepter {

    companion object {
        @Advice.OnMethodEnter
        @JvmStatic
        fun onMethodExecution(@Advice.This(optional = true) instance : HttpRequest?,
                              @Advice.Origin("#m") method: Method){
            createEnterSpan(getCurrentSpan()).apply {
                this.className = instance?.let { it::class.java.name }
                this.methodName = method.name
                this.type = HandlerType.HTTPCLIENT.name
            }

            instance?.header(HEADER_TRACE_ID, getTraceId())
                    ?.header(HEADER_REMOTE_IP, getHostName())
                    ?.header(HEADER_REMOTE_APP, getAppName())
        }

        @Advice.OnMethodExit(onThrowable = Throwable::class)
        @JvmStatic
        fun onMethodExit(@Advice.Thrown throwable: Throwable?) = getCurrentSpanAndRemove(throwable)
    }

}