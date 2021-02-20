package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import org.mermaid.pilog.agent.handler.ServletHandler
import org.mermaid.pilog.agent.handler.getHandler
import java.lang.Exception
import java.lang.reflect.Method

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1910:41
 * @version 1.0
 */
class ServletAdvice {

    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") className: String,
              @Advice.Origin method: Method,
              @Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) req: Any?,
              @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) resp : Any?) {
        getHandler(ServletHandler::class.java).before(className,method, arrayOf(req,resp))
    }

    @Advice.OnMethodExit(onThrowable = Exception::class)
    fun exit(@Advice.Origin("#t") className: String,
             @Advice.Origin method: Method,
             @Advice.AllArguments args: Array<*>?,
             @Advice.Thrown throwable: Throwable) {
        getHandler(ServletHandler::class.java).after(className,method,args,null,throwable)
    }
}