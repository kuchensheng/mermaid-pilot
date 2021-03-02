package org.mermaid.pilog.agent.common

import org.mermaid.pilog.agent.handler.parameterNames
import org.springframework.http.HttpRequest
import java.lang.reflect.Method
import javax.servlet.http.HttpServletRequest

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/211:07
 * @version 1.0
 */

/**
 * 方法参数信息收集
 */
fun collectParameters(method: Method, args: Array<*>?) : MutableMap<String,Any?>?  = mutableMapOf<String,Any?>().apply {
    try {
        method.parameters?.indices?.forEach {
            put(method.parameters[it].name, args?.get(it))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 获取Servlet请求参数
 */
fun getParameterInfo(request: HttpServletRequest): MutableMap<String, Any?>? {
    val parameterMap = hashMapOf<String,Any?>()
    //读取url参数信息
    request.parameterNames?.iterator()?.forEach {
        parameterMap[it] = request.getParameter(it)
    }
    request.headerNames?.iterator()?.forEach {
        if (!parameterNames.contains(it)) parameterMap[it] = request.getHeader(it)
    }
    request.reader.readText()?.let { parameterMap["requestBody"] = it }
    return parameterMap
}

fun getParameterInfo(request: HttpRequest?): MutableMap<String, Any?>?  = hashMapOf<String,Any?>().apply { request?.headers?.filter { !parameterNames.contains(it) }?.run { putAll(this) } }