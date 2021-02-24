package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import org.mermaid.pilog.agent.common.getTraceId
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.handler.collectParameters
import org.mermaid.pilog.agent.handler.getAppName
import org.mermaid.pilog.agent.handler.getHandler
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.mermaid.pilog.agent.plugin.factory.logger
import java.lang.reflect.Method
import java.time.LocalDateTime

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
        val parentId = getCurrentSpan()?.spanId
        createEnterSpan(parentId, getTraceId()).apply {
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