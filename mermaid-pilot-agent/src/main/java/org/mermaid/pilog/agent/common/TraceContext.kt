package org.mermaid.pilog.agent.common

import org.mermaid.pilog.agent.model.TraceContextModel
import org.mermaid.pilog.agent.model.getCurrentSpan
import kotlin.concurrent.getOrSet

/**
 * description: 跟踪日志上下文
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:30
 * @version 1.0
 */
private val traceContext = InheritableThreadLocal<TraceContextModel>()
fun clearAll() = traceContext.remove()
fun set(model: TraceContextModel) = traceContext.set(model)
fun getTraceId() = getOrNew().traceId
fun setTraceId(traceId: String) = getOrNew().let { it.traceId = traceId }
fun getSpanId() = getOrNew().spanId
fun setSpanId(spanId : String) = getOrNew().let { it.spanId = spanId }
fun getOrNew() = traceContext.getOrSet { TraceContextModel() }

fun getCurrentId() = getCurrentSpan()?.spanId?:"nvl"