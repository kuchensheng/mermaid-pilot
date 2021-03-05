package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import net.bytebuddy.implementation.bind.annotation.*
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_APP
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_IP
import org.mermaid.pilog.agent.handler.HEADER_TRACE_ID
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.model.getHostName
import java.lang.Exception
import java.lang.reflect.Method
import kotlin.jvm.Throws

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
    @Throws(Exception::class)
    fun onMethodExecution(@This instance : Any, @Origin method: Method) : Any {
        instance as HttpRequest
        instance.header(HEADER_TRACE_ID, getTraceId()).header(HEADER_REMOTE_IP, getHostName()).header(HEADER_REMOTE_APP, getAppName())
        println("执行类: ${instance::class.java.name}, execute方法: $method")
        val httpRequest = getHttpRequest(instance).also {
            println("$it")
        }
        var m = HttpRequest::class.java.getDeclaredMethod("execute",Boolean::class.java)
        return ("executeAsync" == method.name).let { m.invoke(httpRequest,it) }
    }

    private fun getHttpRequest(instance: HttpRequest) = HttpUtil.createRequest(instance.method,instance.url).apply {
        instance.headers()?.forEach { (t, u) -> header(t,u.first()) }
        instance.toString().let { it.substringAfter("Request Body:").replace("\r\n","").trim().let { b -> this.body(b) } }
    }

}