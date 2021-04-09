package org.mermaid.pilog.agent.advice

import cn.hutool.http.HttpRequest
import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bind.annotation.Super
import org.mermaid.pilog.agent.common.getParameterInfo
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
        instance as HttpRequest
        val isSelf = instance.header("h-self") == "true"
        if (!isSelf) {
            createEnterSpan(getCurrentSpan()).apply {

                this.className = instance::class.java.name
                this.requestMethod = instance.method.name
                this.requestUri = URL(instance.url).toURI().path
                this.parameterInfo = getParameterInfo(instance)
                instance.header(HEADER_TRACE_ID, this.traceId)
                        .header(HEADER_REMOTE_IP, getHostName())
                        .header(HEADER_REMOTE_APP, getAppName())
                this.methodName = method.name
                this.type = HandlerType.HTTPCLIENT.name

            }
        }


    }

    @Advice.OnMethodExit(onThrowable = Throwable::class)
    @JvmStatic
    fun onMethodExit(@Advice.This(optional = true) instance : Any,@Advice.Thrown throwable: Throwable?) {
        if ((instance as HttpRequest).header("h-self") !="true") getCurrentSpanAndRemove(throwable)
    }
}