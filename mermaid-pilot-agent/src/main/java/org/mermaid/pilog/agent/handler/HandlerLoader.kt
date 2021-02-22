package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.core.HandlerName
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2010:44
 * @version 1.0
 */
private val handlerMap = ConcurrentHashMap<String,IHandler>()

//fun getHandler(className: String) :IHandler = handlerMap[className] ?: loadHandler(className)
fun getHandler(clazz: Class<out IHandler>) : IHandler = handlerMap[clazz.name] ?: loadHandler(clazz)

fun loadHandler() {
    HandlerName.values().forEach {
        println("加载handler:${it.clazz?.name}")
        it.clazz?.run { loadHandler(this) }
//        it.className?.run { loadHandler(this) }
    }
}

fun loadHandler(clazz: Class<out IHandler>) : IHandler {
    val instanceKey = "${clazz.name}"
    return handlerMap[instanceKey] ?: clazz.newInstance().apply { handlerMap[instanceKey] = this }
}

//fun loadHandler(className : String) : IHandler {
//
//    return handlerMap[className] ?: Class.forName(className).run {
//        if (this is IHandler) {
//            handlerMap[className] = this
//            return@run this
//        }
//        else throw RuntimeException("$className is not a IHandler")
//    }
//}