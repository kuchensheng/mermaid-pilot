package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import net.bytebuddy.implementation.bytecode.assign.Assigner
import org.mermaid.pilog.agent.handler.ProcessHandler
import org.mermaid.pilog.agent.handler.ServletHandler
import org.mermaid.pilog.agent.handler.getHandler
import java.lang.Exception
import java.lang.reflect.Method

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2218:08
 * @version 1.0
 */
class ProcessAdvice {
    companion object {
        @Advice.OnMethodEnter
        @JvmStatic
        fun enter(@Advice.Origin("#t") className: String,
                  @Advice.Origin method: Method,
                  @Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) req: Any?,
                  @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) resp : Any?) {
            println("processHandler 执行方法：${method.name}")
            getHandler(ProcessHandler::class.java).before(className,method, arrayOf(req,resp))
        }

        @Advice.OnMethodExit
        @JvmStatic
        fun exit(@Advice.Origin("#t") className: String,
                 @Advice.Origin method: Method,
                 @Advice.AllArguments args: Array<*>?) {
            getHandler(ProcessHandler::class.java).after(className,method,args,null,null)
        }
    }
}