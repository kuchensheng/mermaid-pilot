package org.mermaid.pilog.agent.report

import org.mermaid.pilog.agent.common.ReportType
import org.mermaid.pilog.agent.model.Span

abstract class AbstractReport(type: ReportType) : IReporter {
    var type = type
    override fun report(span: Span) = report(arrayListOf(span))

    override fun report(list: List<Span>): Int? = doReport(list)

    abstract fun doReport(list: List<Span>) : Int?
}