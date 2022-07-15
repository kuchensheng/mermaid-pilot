package org.mermaid.pilog.agent.common

import org.mermaid.pilog.agent.model.LogModel
import org.mermaid.pilog.agent.report.AbstractReport
import org.mermaid.pilog.agent.report.LokiReporter
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * description: span信息管理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:24
 * @version 1.0
 */
val threadLocalSpan = ThreadLocal<Stack<LogModel>>()
val blockingQueue = CopyOnWriteArrayList<LogModel>()


fun produce(span: LogModel) = blockingQueue.add(span)

@Synchronized
fun consume() : MutableList<LogModel> {
    return blockingQueue.toMutableList().also { blockingQueue.clear() }
}

fun report(models: List<LogModel>) = getReport(config.reportType).report(models).also {
    if (blockingQueue.isEmpty()) traceIds.remove()
}
private fun getReport(type: ReportType) : AbstractReport = LokiReporter