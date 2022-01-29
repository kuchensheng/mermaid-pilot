package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.common.blockingQueue
import org.mermaid.pilog.agent.model.LogModel
import java.lang.reflect.Method
import java.util.concurrent.LinkedBlockingQueue

object LogBackAdvice {
    private val logInfoQueue = LinkedBlockingQueue<String>(1024)

    @JvmStatic
    @Advice.OnMethodEnter
    fun enter(@Advice.Origin method: Method,@Advice.AllArguments args : Array<*>) {
//        println("执行方法${method.name}")
        //todo 生成日志
        val content = args[0].toString()
        val logInfo = LogInfo().apply { this.content = content }
        blockingQueue.add(logInfo)
    }


}

class LogInfo : LogModel()