package org.mermaid.pilog.agent.trace

import net.bytebuddy.asm.Advice
import java.time.LocalDateTime

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1011:45
 * @version 1.0
 */
class TraceHandler {

    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") calssName:String, @Advice.Origin("#m") methodName:String) {
        val span = createEntrySpan().apply {
            this.className = className
            this.methodName = methodName
            this.startTime = LocalDateTime.now()
        }
        println("链路跟踪：${span.traceId},className:${span.className},methodName:${span.methodName}")
    }

    @Advice.OnMethodExit
    fun exit(@Advice.Origin("#t") className: String,@Advice.Origin("#m") methodName: String) {
        val span = getExitSpan()
        println("链路跟踪（MQ）：${span?.traceId},className is ${span?.className},methodName is ${span?.methodName}")
    }
}