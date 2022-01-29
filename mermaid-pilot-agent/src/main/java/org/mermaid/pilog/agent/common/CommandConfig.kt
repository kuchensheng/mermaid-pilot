package org.mermaid.pilog.agent.common

fun readCommandLineArgs(args: List<String>?) {
    println("命令行参数，args = $args,size = ${args?.size}")
    args?.forEach { arg ->
        println("args:$arg")
        arg.split("=").let {
            when (it[0]) {
                "report.service.host" -> CommandConfig.serviceHost = it[1]
                "report.service.url" -> CommandConfig.serviceUri = it[1]
                "report.service.type" -> it[1]?.let { v ->  CommandConfig.reportType = ReportType.valueOf(v) }
                "spring.application.name" -> CommandConfig.appName = it[1]
            }
        }
    }
}

object CommandConfig {
    var reportType : ReportType = ReportType.LOKI
    var serviceHost : String = "http://loki-service:3100"
    var serviceUri : String = "/loki/api/v1/push"
    var appName : String? = null
}

enum class ReportType {
    LOKI,KAFKA
}