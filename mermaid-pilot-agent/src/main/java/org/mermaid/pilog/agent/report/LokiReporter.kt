package org.mermaid.pilog.agent.report

import net.sf.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.mermaid.pilog.agent.advice.LogInfo
import org.mermaid.pilog.agent.common.ReportType
import org.mermaid.pilog.agent.common.config
import org.mermaid.pilog.agent.model.LogModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

object LokiReporter : AbstractReport(ReportType.LOKI) {
    private val logger: Logger = LoggerFactory.getLogger(LokiReporter::class.java)

    private val mediaType = "application/json".toMediaType()
    private var httpClient : OkHttpClient = OkHttpClient.Builder().callTimeout(3,TimeUnit.SECONDS).writeTimeout(5,TimeUnit.SECONDS).build()
    override fun doReport(list: List<LogModel>): Int {
        if (list.isNullOrEmpty()) return 0
        //每次上传16条,否则可能会日志过大
        (0 until max(1,(list.size /4) + 1)).forEach { doLogPush(list.subList(it * 4,min((it+1) *4,list.size))) }
        return 0
    }

    private fun getLocalTime() = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()

    private fun doLogPush(list: List<LogModel>) {
        var host = config.serviceHost
        if (host.endsWith("/")) {
            host = host.dropLast(1)
        }
        var uri = config.serviceUri
        if (!uri.startsWith("/")){
            uri = "/$uri"
        }

        var lokiService = "$host$uri"
        if (!lokiService.startsWith("http")) lokiService = "http://$lokiService"
        val logArray = list.map { model ->
            if (!model.tags.contains("job")) {
                model.tags["job"] = "traceInfos"
            }
            model.tags.apply {
                this["podIp"] = Inet4Address.getLocalHost().hostName
                config.appName?.let { this["appName"] = it }
                this["stream"] = "sdk"
            }
//            logger.info("model.tags = ${model.tags}")
            mutableMapOf<String,Any>().apply {
                val values = arrayListOf("${getLocalTime()}000000","${model.content}")
                this["stream"] = model.tags
                this["values"] = arrayListOf(values)
            }
        }.toMutableList()

        val requestBodyStr = JSONObject().apply { put("streams",logArray) }.toString()
//        logger.info("requestBody = $requestBodyStr")
        val requestBody = requestBodyStr.toRequestBody()
        val request = Request.Builder()
            .url(lokiService)
            .post(requestBody)
            .addHeader("Accept-Encoding","gzip, deflate, br")
            .addHeader("Content-Type","application/json")
            .addHeader("Connection","keep-alive")
            .addHeader("Content-Length",requestBody.contentLength().toString())
            .build()
        httpClient?.run {
            val execute = newCall(request).execute()
            execute.use { it.body?.string() }
        }
    }
}

fun main() {
    config.serviceHost = "10.30.30.87:3100"
    val list = mutableListOf<LogModel>().apply {
        val logInfo = LogInfo()
        logInfo.content = "哈哈哈哈"
        add(logInfo)
    }
    LokiReporter.doReport(list)
}