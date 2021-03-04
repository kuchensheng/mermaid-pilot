package org.mermaid.pilog.agent.advice

import cn.hutool.http.HttpRequest
import net.bytebuddy.asm.Advice
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
    fun onMethodExecution(@Advice.This(optional = true) instance : Any?,
                          @Advice.Origin("#m") method: Method){
        createEnterSpan(getCurrentSpan()).apply {
            this.className = instance?.let { it::class.java.name }
            this.methodName = method.name
            this.type = HandlerType.HTTPCLIENT.name
        }

        instance?.run {
            if (instance is HttpRequest) {
                instance.header(HEADER_TRACE_ID, getTraceId())
                        .header(HEADER_REMOTE_IP, getHostName())
                        .header(HEADER_REMOTE_APP, getAppName())
            }
        }

    }

    @Advice.OnMethodExit(onThrowable = Throwable::class)
    @JvmStatic
    fun onMethodExit(@Advice.Thrown throwable: Throwable?) = getCurrentSpanAndRemove(throwable)
}