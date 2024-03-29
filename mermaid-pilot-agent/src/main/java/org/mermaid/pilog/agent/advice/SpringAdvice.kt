package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.handler.SpringHandler
import org.mermaid.pilog.agent.handler.getHandler
import java.lang.reflect.Method

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2218:08
 * @version 1.0
 */
object SpringAdvice {
    @Advice.OnMethodEnter
    @JvmStatic
    fun enter(@Advice.Origin("#t") className: String,
              @Advice.Origin method: Method,
              @Advice.AllArguments args: Array<*>?) {
        getHandler(SpringHandler::class.java).before(className,method, args)
    }

    @Advice.OnMethodExit(onThrowable = Throwable::class)
    @JvmStatic
    fun exit(@Advice.Origin("#t") className: String,
             @Advice.Origin method: Method,
             @Advice.AllArguments args: Array<*>?,
             @Advice.Thrown throwable: Throwable?) {
        getHandler(SpringHandler::class.java).after(className,method,args,null,throwable)
    }
}