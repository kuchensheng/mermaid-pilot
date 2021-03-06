package org.mermaid.pilog.agent.report

import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import org.mermaid.pilog.agent.common.CommandConfig
import org.mermaid.pilog.agent.common.ReportType
import org.mermaid.pilog.agent.model.Span
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import java.net.Inet4Address
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.ZoneOffset

object LokiReporter : AbstractReport(ReportType.LOKI) {
    private val logger: Logger = LoggerFactory.getLogger(LokiReporter::class.java)

    override fun doReport(list: List<Span>): Int? {
        val lokiService = "${CommandConfig.serviceHost.let { if (it.endsWith("/")) it.dropLast(1) else it }}${CommandConfig.serviceUri.let { if (!it.startsWith("/")) "/$it" else it }}".also { logger.debug("跟踪信息上报到Loki,服务地址：$it") }
        (0 until maxOf(1,(list.size /16).toInt())).forEach { _ ->
            JSONArray().apply {
                list.forEach { span ->
                    span.parameterInfo = mutableMapOf()
                    add(JSONObject().apply {
                        //tags
                        this["stream"] = mutableMapOf<String,String>().apply {
                            CommandConfig.appName?.let { this["appName"] = it }
                            this["podIp"] = Inet4Address.getLocalHost().hostName
                            this["job"] = "traceInfos"
//                            this["traceId"] = span.traceId
                        }
                        this["values"] = mutableListOf(mutableListOf("${getLocalTime()}000000").apply {
                            var idx = 0
                            val content = span.toString()
                            (0 until content.length / 216).forEach {
                                add(span.toString().substring(idx, maxOf(span.toString().length,idx+218*it)))
                            }
                        })
                    })
                }
            }.let { JSONObject().apply { this["streams"] = it }.toString().toByteArray(Charsets.UTF_8) }.run {
                HttpUtil.createPost(lokiService)
                        .header(Header.CONTENT_ENCODING,"gzip")
                        .header(Header.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                        .header("h-self","true")
                        .timeout(1000)
                        .body(this)
                        .execute()
            }.run {
                logger.info("日志上报状态：${this.isOk},响应内容：${this.body()}")
            }
        }

        return 0
    }

    private fun getLocalTime() = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli().toString()
}