package org.mermaid.pilog.agent.util

import org.mermaid.pilog.agent.report.LokiReporter
import java.time.LocalDateTime
import java.time.ZoneOffset

object HttpUtil {

    private val STREAM = "stream"
    private val VALUES = "values"
    private val STREAMS = "streams"
    private val JOB = "job"



}

fun main() {
    val logArray = mutableListOf<Map<String,Any>>()
    val map = mutableMapOf<String,Any>().apply {
        val values = arrayListOf("${LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()}000000",""""{"os_free_physical_memory_bytes":4079218688,"os_committed_virtual_memory_bytes":1339662336,"os_total_swap_space_bytes":26181644288,"os_system_load_average":-1,"os_total_physical_memory_bytes":17054838784,"os_system_cpu_load":0,"os_free_swap_space_bytes":7464759296,"os_available_processors":6,"os_process_cpu_load":0.26045963,"os_objectName":"java.lang:type=OperatingSystem","os_memory_usage":31685664768}"""")
        this["stream"] = mutableMapOf("job" to "test")
        this["values"] = arrayListOf(values)
    }
    logArray.add(map)
    val requestBodyStr = mutableMapOf("streams" to logArray).toString()
    println(requestBodyStr)
}