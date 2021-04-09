package org.mermaid.pilog.agent.common

import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.report.AbstractReport
import org.mermaid.pilog.agent.report.ReportStrategy
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max

/**
 * description: span信息管理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:24
 * @version 1.0
 */
val threadLocalSpan = ThreadLocal<Stack<Span>>()
private val blockingQueue = CopyOnWriteArrayList<Span>()


fun produce(span: Span) = blockingQueue.add(span)

@Synchronized
fun consume() : MutableList<Span> {
    return blockingQueue.toMutableList().also { blockingQueue.clear() }
}

fun report(spans: List<Span>) = getReport(CommandConfig.reportType).report(spans).also {
    if (blockingQueue.isEmpty()) traceIds.remove()
}
private fun getReport(type: ReportType) : AbstractReport = ReportStrategy().getReporter(type)