package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.handler.SpringWebHandler
import org.mermaid.pilog.agent.handler.getHandler
import org.mermaid.pilog.agent.plugin.factory.logger
import java.lang.reflect.Method

/**
 * description: Spring Controller / RestController 方法处理前后的通知
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2010:36
 * @version 1.0
 */
class SpringWebAdvice {
    companion object {
        @Advice.OnMethodEnter
        @JvmStatic
        fun enter(@Advice.Origin("#t") className: String,
                  @Advice.Origin method: Method) {
            logger.info("执行SpringWebAdvice")
            getHandler(SpringWebHandler::class.java).before(className,method, arrayOf(""))
        }

        @Advice.OnMethodExit
        @JvmStatic
        fun exit(@Advice.Origin("#t") className: String,
                 @Advice.Origin method: Method,
                 @Advice.AllArguments args: Array<*>?) {
            getHandler(SpringWebHandler::class.java).after(className,method,args,null,null)
        }
    }
}