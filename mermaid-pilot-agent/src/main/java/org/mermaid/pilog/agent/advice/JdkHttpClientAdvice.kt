package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.common.collectParameters
import org.mermaid.pilog.agent.model.createEnterSpan
import org.mermaid.pilog.agent.model.getCurrentSpan
import org.mermaid.pilog.agent.model.getCurrentSpanAndRemove
import org.mermaid.pilog.agent.plugin.factory.logger
import java.lang.reflect.Method

/**
 * description: JdkAdvice操作
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/210:26
 * @version 1.0
 */
object JdkHttpClientAdvice {
    @JvmStatic
    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") className: String?, @Advice.Origin method: Method, @Advice.AllArguments args: Array<*>?)  {
        logger.info("执行JDKHttpClient advice,methodName = ")
        createEnterSpan(getCurrentSpan()).apply {
            this.className = className
            this.methodName = method.name
            if (method.name == "setRequestMethod") args?.let { this.requestMethod = args[0]?.toString() }
            if (method.name == "setRequestProperty" || method.name == "addRequestProperty") collectParameters(method,args)?.let { it.forEach { (t, value) -> this.parameterInfo!![t] = value } }

        }
    }

    @JvmStatic
    @Advice.OnMethodExit(onThrowable = Throwable::class)
    fun exit(@Advice.Thrown throwable: Throwable?)  = getCurrentSpanAndRemove(throwable)
}