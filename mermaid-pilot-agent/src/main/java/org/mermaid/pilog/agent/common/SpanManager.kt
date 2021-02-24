package org.mermaid.pilog.agent.common

import net.sf.json.JSONObject
import org.mermaid.pilog.agent.core.HandlerType
import org.mermaid.pilog.agent.model.Span
import org.mermaid.pilog.agent.report.IReporter
import org.mermaid.pilog.agent.report.getReporter
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock

/**
 * description: span信息管理
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:24
 * @version 1.0
 */
val threadLocalSpan = ThreadLocal<Stack<Span>>()
val blockingQueue = LinkedBlockingQueue<Span>(10240)

fun createEntrySpan(type: HandlerType?,rpcId: String?) = createSpan(type,rpcId)

fun produce(span: Span) = blockingQueue.add(span)

fun consume() : Span? = blockingQueue.take()

private fun createSpan(type: HandlerType?,rpcId: String?) {
    var stack = threadLocalSpan.get()
    if (stack.isNullOrEmpty()) threadLocalSpan.set(Stack<Span>().apply { stack = this })
    if (stack.isEmpty()){}
}

private val lock = ReentrantLock()
fun report(span: Span) {
    getReport("").run {
        lock.lock()
        try {
            report(span)
        } finally {
            lock.unlock()
        }
    }
}

private fun getReport(reportName: String?) : IReporter = getReporter(reportName)?: object : IReporter {
    override fun report(span: Span): Int?  = println(span.toString()).let {
        println("队列剩余量：${blockingQueue.size}")
        0 }

    override fun report(list: List<Span>): Int?  = println(JSONObject.fromObject(list)).let { 0 }
}
