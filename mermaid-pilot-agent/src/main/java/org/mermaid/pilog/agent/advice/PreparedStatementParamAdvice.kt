package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.common.collectParameters
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import java.lang.reflect.Method

/**
 * description: jdbc执行通知
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2316:38
 * @version 1.0
 */
object PreparedStatementParamAdvice {
    @Advice.OnMethodEnter
    @JvmStatic
    fun enter(@Advice.Origin("#t") className: String,
              @Advice.Origin method: Method,
              @Advice.AllArguments args: Array<*>) {
        createEnterSpan(getCurrentSpan()).apply {
            this.methodName = method.name
            this.type = HandlerType.JDBC.name
            this.parameterInfo = collectParameters(method,args)
            this.className = className
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable::class)
    @JvmStatic
    fun exit(@Advice.Thrown throwable: Throwable?) {
        getCurrentSpanAndRemove(throwable)
    }
}