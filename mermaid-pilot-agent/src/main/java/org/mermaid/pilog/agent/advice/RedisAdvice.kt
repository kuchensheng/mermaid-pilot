package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.handler.collectParameters
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import java.lang.reflect.Method

/**
 * description: Redis 操作通知处理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2618:55
 * @version 1.0
 */
object RedisAdvice {
    @JvmStatic
    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") className : String,
              @Advice.Origin method: Method,
              @Advice.AllArguments args: Array<*>) {
        createEnterSpan(getCurrentSpan()).apply {
            this.methodName = method.name
            this.className = className
            this.parameterInfo = collectParameters(method,args)
            this.type = HandlerType.REDIS.name
        }
    }

    @JvmStatic
    @Advice.OnMethodExit(onThrowable = Throwable::class)
    fun exit(@Advice.Thrown throwable: Throwable?) {
        getCurrentSpanAndRemove(throwable)
    }
}