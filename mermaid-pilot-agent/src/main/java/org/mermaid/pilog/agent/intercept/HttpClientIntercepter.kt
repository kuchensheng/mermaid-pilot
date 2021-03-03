package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.implementation.bind.annotation.This
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_APP
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_IP
import org.mermaid.pilog.agent.handler.HEADER_TRACE_ID
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.mermaid.pilog.agent.model.getHostName
import java.lang.Exception
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
object HttpClientIntercepter {

    @RuntimeType
    @JvmStatic
    fun intercept(@Origin method: Method,@SuperCall callable: Callable<*>) : Any? {
        println("监控方法")
//        if (method.name == "execute" && instance is HttpRequest) {
//            println("执行方法：${method.name}")
//            createEnterSpan(getCurrentSpan()).apply {
//                this.className = instance::class.java.name
//                this.methodName = method.name
//            }
//           instance.header(HEADER_TRACE_ID, getTraceId())
//                   .header(HEADER_REMOTE_IP, getHostName())
//                   .header(HEADER_REMOTE_APP, getAppName())
//            var throwable : Throwable? = null
//            try {
//                return callable.call()
//            } catch (e : Exception) {
//                e.printStackTrace()
//                throwable = e
//            }finally {
//                getCurrentSpanAndRemove(throwable)
//            }
//        }
        return callable.call()
    }
}