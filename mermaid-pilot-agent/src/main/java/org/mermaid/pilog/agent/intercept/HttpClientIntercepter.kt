package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import com.sun.org.apache.xpath.internal.operations.Bool
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodCall
import net.bytebuddy.implementation.bind.annotation.*
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_APP
import org.mermaid.pilog.agent.handler.HEADER_REMOTE_IP
import org.mermaid.pilog.agent.handler.HEADER_TRACE_ID
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.model.getHostName
import java.lang.Exception
import java.lang.reflect.Method
import java.lang.reflect.Modifier
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
        val my = instance.header(HEADER_TRACE_ID, getTraceId()).header(HEADER_REMOTE_IP, getHostName()).header(HEADER_REMOTE_APP, getAppName())
        println("执行类: ${my::class.java.name}, execute方法: $method")
        println("$my")
        val m = HttpRequest::class.java.getDeclaredMethod("execute",Boolean::class.java)
        return ("executeAsync" == method.name).let { m.invoke(my,it) }
    }

}