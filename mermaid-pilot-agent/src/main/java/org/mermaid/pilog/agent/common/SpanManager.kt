package org.mermaid.pilog.agent.common

import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import java.util.*

/**
 * description: span信息管理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:24
 * @version 1.0
 */
val threadLocalSpan = ThreadLocal<Stack<Span>>()

fun createEntrySpan(type: HandlerType?,rpcId: String?) = createSpan(type,rpcId)

private fun createSpan(type: HandlerType?,rpcId: String?) {
    var stack = threadLocalSpan.get()
    if (stack.isNullOrEmpty()) threadLocalSpan.set(Stack<Span>().apply { stack = this })
    if (stack.isEmpty()){}
}
