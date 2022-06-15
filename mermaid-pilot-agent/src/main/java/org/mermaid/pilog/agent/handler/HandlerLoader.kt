package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.core.HandlerName
import org.mermaid.pilog.agent.runner.JvmRunner
import java.util.concurrent.ConcurrentHashMap

/**
 * description: 处理器加载
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2010:44
 * @version 1.0
 */
private val handlerMap = ConcurrentHashMap<String,IHandler>()

fun getHandler(clazz: Class<out IHandler>) : IHandler = handlerMap[clazz.name] ?: loadHandler(clazz)

fun loadHandler() {
    HandlerName.values().forEach {
//        println("加载handler:${it.clazz?.name}")
        it.clazz?.run { loadHandler(this) }
    }
}

fun loadHandler(clazz: Class<out IHandler>) : IHandler {
    val instanceKey = "${clazz.name}"
    return handlerMap[instanceKey] ?: clazz.newInstance().apply { handlerMap[instanceKey] = this }
}

fun loadRunner() {
    JvmRunner
}