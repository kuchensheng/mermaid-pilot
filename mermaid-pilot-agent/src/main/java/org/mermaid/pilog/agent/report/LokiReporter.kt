package org.mermaid.pilog.agent.report

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.mermaid.pilog.agent.common.CommandConfig
import org.mermaid.pilog.agent.common.ReportType
import org.mermaid.pilog.agent.model.LogModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

object LokiReporter : AbstractReport(ReportType.LOKI) {
    private val logger: Logger = LoggerFactory.getLogger(LokiReporter::class.java)

    private val mediaType = "application/json".toMediaType()
    private var httpClient : OkHttpClient? = null
    override fun doReport(list: List<LogModel>): Int? {
        httpClient?:run {
            httpClient = OkHttpClient().newBuilder().callTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(5,TimeUnit.SECONDS)
                .build();
        }
        val lokiService = "${CommandConfig.serviceHost.let { if (it.endsWith("/")) it.dropLast(1) else it }}${CommandConfig.serviceUri.let { if (!it.startsWith("/")) "/$it" else it }}".also { logger.debug("跟踪信息上报到Loki,服务地址：$it") }
        val logArray = JSONArray()
        list.forEach { model ->
            model.tags.apply {
                this["podIp"] = Inet4Address.getLocalHost().hostName
                CommandConfig.appName?.let { this["appName"] = it }
                this["job"] = "traceInfos"
                this["time"] = getLocalTime()
            }
            val map = mutableMapOf<String,Any>().apply {
                this["stream"] = model.tags
                this["values"] = JSONObject.fromObject(model).toString()
            }
            logArray.add(map)
        }
        val requestBody = logArray.toString().toRequestBody(mediaType)
        val headers = Headers.Builder()
            .add("Accept-Encoding","gzip")
            .add("Content-Type","application/json")
            .add("h-self","true")
            .add("Connection","keep-alive")
            .build()
        val request = Request.Builder()
            .url(lokiService)
            .post(requestBody)
            .headers(headers)
            .build()
        val result = httpClient?.run {
            val execute = newCall(request).execute()
            execute.body?.string() ?:""
        }
        println("日志上报结果:$result")

        return 0
    }

    private fun getLocalTime() = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli().toString()
}