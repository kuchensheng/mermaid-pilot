package org.mermaid.pilog.agent.core

import org.mermaid.pilog.agent.handler.*

/**
 * description: 处理器枚举
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2010:52
 * @version 1.0
 */
enum class HandlerName {

    PROCESS("process",ProcessHandler::class.java,""),
    THREAD("thread",ThreadHandler::class.java,"")
    ;
    var code : String = ""
    var clazz: Class<out IHandler>? = null
    var className : String? = null

    @JvmOverloads
    constructor(code: String,clazz: Class<out IHandler>?, className: String?) {
        this.code = code
        this.clazz = clazz
        this.className = className
    }


}