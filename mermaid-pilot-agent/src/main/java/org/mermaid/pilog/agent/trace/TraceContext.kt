package org.mermaid.pilog.agent.trace

import java.util.*

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1012:07
 * @version 1.0
 */
class TraceContext {
    companion object {
        val traceLocal = ThreadLocal<String>()
    }
}
fun clear() = TraceContext.traceLocal.remove()

fun getLinkId() = TraceContext.traceLocal.get()

fun setLinkId(linkId:String) = TraceContext.traceLocal.set(linkId)

fun generateLinkId() = UUID.randomUUID().toString()

fun generateSpanId() = UUID.randomUUID().toString()