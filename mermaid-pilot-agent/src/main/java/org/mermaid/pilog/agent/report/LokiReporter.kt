package org.mermaid.pilog.agent.report

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.mermaid.pilog.agent.advice.LogInfo
import org.mermaid.pilog.agent.common.CommandConfig
import org.mermaid.pilog.agent.common.ReportType
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
    private var httpClient : OkHttpClient? = null
    override fun doReport(list: List<LogModel>): Int? {
        if (list.isNullOrEmpty()) return 0
//        println("日志上报")
        httpClient?:run {
            httpClient = OkHttpClient().newBuilder().callTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(5,TimeUnit.SECONDS)
                .build()
        }
        //每次上传16条,否则可能会日志过大
        (0 until max(1,(list.size /4) + 1)).forEach {
            val start = it * 4;
            val end = min((it+1) *4,list.size)
            list.subList(start,end).run { doLogPush(this) }
        }

        return 0
    }

    private fun getLocalTime() = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()

    private fun doLogPush(list: List<LogModel>) {
        var lokiService = "${CommandConfig.serviceHost.let { if (it.endsWith("/")) it.dropLast(1) else it }}${CommandConfig.serviceUri.let { if (!it.startsWith("/")) "/$it" else it }}".also { logger.debug("跟踪信息上报到Loki,服务地址：$it") }
        if (!(lokiService.startsWith("https://") || lokiService.startsWith("http://"))) lokiService = "http://"+lokiService;
        val logArray = mutableListOf<Map<String,Any>>()
        list.forEach { model ->

            if (!model.tags.contains("job")) {
                model.tags["job"] = "traceInfos"
            }
            model.tags.apply {
                this["podIp"] = Inet4Address.getLocalHost().hostName
                CommandConfig.appName?.let { this["appName"] = it }
                this["stream"] = "sdk"
            }
            logger.info("model.tags = ${model.tags}")
            val map = mutableMapOf<String,Any>().apply {
                val values = arrayListOf("${getLocalTime()}000000","${model.content}")
                this["stream"] = model.tags
                this["values"] = arrayListOf(values)
            }
            logArray.add(map)
        }

        val requestBodyStr = JSONObject().apply { put("streams",logArray) }.toString()
        logger.info("requestBody = $requestBodyStr")
        val requestBody = requestBodyStr.toRequestBody()
        val request = Request.Builder()
            .url(lokiService)
            .post(requestBody)
            .addHeader("Accept-Encoding","gzip, deflate, br")
            .addHeader("Content-Type","application/json")
            .addHeader("Connection","keep-alive")
            .addHeader("Content-Length",requestBody.contentLength().toString())
            .build()
        val result = httpClient?.run {
            val execute = newCall(request).execute()
            execute.use { it.body?.string() }
        }
        logger.info("日志上报结果:$result \n")
    }
}

fun main() {
    CommandConfig.serviceHost = "10.30.30.87:3100"
    val list = mutableListOf<LogModel>().apply {
        val logInfo = LogInfo()
        logInfo.content = "哈哈哈哈"
        add(logInfo)
    }
    LokiReporter.doReport(list)
}