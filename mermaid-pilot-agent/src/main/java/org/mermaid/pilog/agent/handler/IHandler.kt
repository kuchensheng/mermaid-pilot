package org.mermaid.pilog.agent.handler

import org.mermaid.pilog.agent.model.Span
import java.lang.reflect.Method

/**
 * description: 进出方法通知处理器
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1910:53
 * @version 1.0
 */
interface IHandler {

    /**
     * 方法调用前处理
     */
    fun before(className: String?,method: Method,args:Array<*>?) : Span

    /**
     * 方法调用后处理
     */
    fun after(className: String?,method: Method,array: Array<*>?,result: Any?,thrown: Throwable?)
}