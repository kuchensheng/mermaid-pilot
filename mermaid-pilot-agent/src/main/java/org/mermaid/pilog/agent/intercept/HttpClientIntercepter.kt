package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import net.bytebuddy.implementation.bind.annotation.*
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_APP
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_IP
import org.mermaid.pilog.agent.handler.HEADER_TRACE_ID
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getHostName
import java.lang.reflect.Method

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/315:23
 * @version 1.0
 */
object HttpClientIntercepter {

    @RuntimeType
    @JvmStatic
    fun onMethodExecution(@This instance : HttpRequest, @FieldValue method: Method, @AllArguments args : Array<Any?>?) : Any {
        createEnterSpan(getCurrentSpan()).apply {
            this.className = instance::class.java.name
            this.methodName = "execute"
        }
        instance.header(HEADER_TRACE_ID, getTraceId())
                .header(HEADER_REMOTE_IP, getHostName())
                .header(HEADER_REMOTE_APP, getAppName())
        println("执行方法：${method.name}")
        return method.invoke(instance,args)
    }
}