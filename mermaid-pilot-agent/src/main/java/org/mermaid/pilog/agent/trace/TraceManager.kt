package org.mermaid.pilog.agent.trace

import org.mermaid.pilog.agent.trace.TraceManager.Companion.createSpan
import org.mermaid.pilog.agent.trace.TraceManager.Companion.trace
import java.time.LocalDateTime
import java.util.*

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1012:08
 * @version 1.0
 */
class TraceManager {

    companion object {
        val trace = ThreadLocal<Stack<Span>>()

        fun createSpan(): Span {
            var stack = trace.get()
            if (null == stack) {
                trace.set(Stack())
            }
            var linkedId:String
            if (stack.isEmpty()) {
                linkedId = getLinkId()
                if (null == linkedId) {
                    linkedId = generateLinkId()
                    setLinkId(linkedId)
                }
            } else {
                linkedId = stack.peek().traceId
                setLinkId(linkedId)
            }

            return Span(linkedId).apply {
                spanId = generateSpanId()
                startTime = LocalDateTime.now()
            }
        }
    }

}


fun createEntrySpan() : Span {
    val span = createSpan()
    val stack = trace.get()
    stack.push(span)
    return span
}

fun getExitSpan() : Span? {
    val stack = trace.get()
    if (stack.isNullOrEmpty()) {
        clear()
        return null
    }
    return stack.pop().apply { this.endTime = LocalDateTime.now() }
}
fun getCurrentSpan() : Span? {
    val stack = trace.get()
    if (stack.isNullOrEmpty()) {
        return null
    }
    return stack.peek()
}