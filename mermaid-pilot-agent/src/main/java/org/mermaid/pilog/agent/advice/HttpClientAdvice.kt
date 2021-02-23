package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.handler.HttpClientHandler
import org.mermaid.pilog.agent.handler.getHandler
import java.lang.reflect.Method

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2216:57
 * @version 1.0
 */
class HttpClientAdvice {
    companion object {
        @JvmStatic
        @Advice.OnMethodEnter
        fun enter(@Advice.Origin("#t") className: String?, @Advice.Origin method: Method, @Advice.AllArguments args: Array<*>) {
            getHandler(HttpClientHandler::class.java).before(className,method,args)
        }

        @JvmStatic
        @Advice.OnMethodExit(onThrowable = Throwable::class)
        fun exit(@Advice.Origin("#t") className: String?, @Advice.Origin method: Method, @Advice.AllArguments args: Array<*>, @Advice.Thrown throwable: Throwable) {
            getHandler(HttpClientHandler::class.java).after(className,method,args,null,throwable)
        }
    }
}