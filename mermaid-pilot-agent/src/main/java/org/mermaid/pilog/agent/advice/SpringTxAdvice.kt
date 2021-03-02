package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.common.collectParameters
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import java.lang.reflect.Method

/**
 * description: Spring 事务执行前后的通知
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/249:49
 * @version 1.0
 */

object SpringTxAdvice {
    @JvmStatic
    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") className : String,
         @Advice.Origin method: Method,
         @Advice.AllArguments args: Array<*>) {
        createEnterSpan(getCurrentSpan()).apply {
            this.methodName = method.name
            this.className = className
            this.parameterInfo = collectParameters(method,args)
            this.type = HandlerType.SPRINGTXBEGIN.name
        }
    }

    @JvmStatic
    @Advice.OnMethodExit(onThrowable = Throwable::class)
    fun exit(@Advice.Thrown throwable: Throwable?) {
        getCurrentSpanAndRemove(throwable)
    }
}