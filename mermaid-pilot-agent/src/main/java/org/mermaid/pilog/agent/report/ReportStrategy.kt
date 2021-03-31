package org.mermaid.pilog.agent.report

import org.mermaid.pilog.agent.common.ReportType
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
open class ReportStrategy :  ApplicationContextAware{

    private var beanMap: MutableMap<ReportType,AbstractReport> = mutableMapOf()
    override fun setApplicationContext(p0: ApplicationContext) {
        beanMap.putAll(mutableMapOf<ReportType,AbstractReport>().apply {
            p0.getBeansOfType(AbstractReport::class.java).map { it.value.type to it.value }.toMap().toMutableMap()
        })

    }

    fun getReporter(type: ReportType)  = beanMap[type]?: LokiReporter
}