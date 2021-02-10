package org.mermaid.pilog.agent.trace

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1012:07
 * @version 1.0
 */
class TraceContext {
    val traceLocal = ThreadLocal<String>()

    fun clear() = traceLocal.remove()

    fun getLinkId() = traceLocal.get()

    fun setLinkId(linkId:String) = traceLocal.set(linkId)
}