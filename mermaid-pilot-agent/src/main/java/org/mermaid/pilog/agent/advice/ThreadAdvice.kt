package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.handler.ThreadHandler
import org.mermaid.pilog.agent.handler.getHandler
import java.lang.reflect.Method

/**
 * description: 线程执行通知
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2310:31
 * @version 1.0
 */
object ThreadAdvice {
    @Advice.OnMethodEnter
    @JvmStatic
    fun enter(@Advice.Origin("#t") className: String,
              @Advice.Origin method: Method,
              @Advice.Argument(value = 0,readOnly = false,typing = Assigner.Typing.DYNAMIC) task: Any?) {
        //todo 线程处理，暂时未知是否需要方法退出时处理
        getHandler(ThreadHandler::class.java).before(className,method, task?.let { arrayOf(task) }).run { produce(this) }

    }

    @Advice.OnMethodExit
    @JvmStatic
    fun exit(@Advice.Origin("#t") className: String,
             @Advice.Origin method: Method,
             @Advice.Argument(value = 0,readOnly = false,typing = Assigner.Typing.DYNAMIC) task: Any?) {
        println("线程执行完毕，className: $className,methodName: ${method.name}, task : ${task?.javaClass?.name}")
    }
}