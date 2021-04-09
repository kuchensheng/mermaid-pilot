package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import net.bytebuddy.implementation.bind.annotation.*
import org.mermaid.pilog.agent.common.generateTraceId
import org.mermaid.pilog.agent.common.setTraceId
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
        val my = instance.header(HEADER_TRACE_ID, generateTraceId().also { setTraceId(it) }).header(HEADER_REMOTE_IP, getHostName()).header(HEADER_REMOTE_APP, getAppName())
        val m = HttpRequest::class.java.getDeclaredMethod("execute",Boolean::class.java)
        return ("executeAsync" == method.name).let { m.invoke(my,it) }
    }

}