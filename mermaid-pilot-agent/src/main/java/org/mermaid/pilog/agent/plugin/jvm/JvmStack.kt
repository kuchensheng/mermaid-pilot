package org.mermaid.pilog.agent.plugin.jvm

import com.sun.management.OperatingSystemMXBean
import com.sun.management.ThreadMXBean
import net.sf.json.JSONObject
import org.mermaid.pilog.agent.common.CommandConfig
import org.mermaid.pilog.agent.common.blockingQueue
import org.mermaid.pilog.agent.model.LogModel
import org.mermaid.pilog.agent.plugin.factory.logger
import java.lang.management.*

/**
 * description: jvm堆栈信息
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1818:20
 * @version 1.0
 */
private val osmxb = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
private val jvmmxb = ManagementFactory.getMemoryMXBean()
fun memory() {
    //获取总物理内存 = 物理内存 + 虚拟内存
    val totalVirtualMemory = osmxb.totalSwapSpaceSize
    val totalPhysicalMemorySize = osmxb.totalPhysicalMemorySize
    val total = totalVirtualMemory + totalPhysicalMemorySize

    //剩余内存 = 物理内存 + 虚拟内存
    val freePhysicalMemorySize = osmxb.freePhysicalMemorySize
    val freeVirtualMemory = osmxb.freeSwapSpaceSize

    val free = freeVirtualMemory + freePhysicalMemorySize
    //内存使用量 = 物理内存 + 虚拟内存
    val usageMemory = total - free

    //内存使用率
//    val compare = String.format("%.2f",usageMemory).toFloat()

    val jsonObject = JSONObject()
    jsonObject["os_free_physical_memory_bytes"] = freePhysicalMemorySize
    jsonObject["os_committed_virtual_memory_bytes"] = osmxb.committedVirtualMemorySize
    jsonObject["os_total_swap_space_bytes"] = totalVirtualMemory
    jsonObject["os_system_load_average"] = osmxb.systemLoadAverage
    jsonObject["os_total_physical_memory_bytes"] = totalPhysicalMemorySize
    jsonObject["os_system_cpu_load"] = osmxb.systemCpuLoad
    jsonObject["os_free_swap_space_bytes"] = osmxb.freeSwapSpaceSize
    jsonObject["os_available_processors"] = osmxb.availableProcessors
    jsonObject["os_process_cpu_load"] = osmxb.processCpuLoad
    jsonObject["os_objectName"] = osmxb.objectName.toString()
    jsonObject["os_memory_usage"] = usageMemory

    blockingQueue.add(JvmInfo().apply {
        tags["application_name"] = CommandConfig.appName?: "default_name"
        tags["job"] = "os_info"
        content = """${jsonObject.toString().replace("{","\"{").replace("}","}\"")}"""
    })
}
fun printMemoryInfo() {
    logger.info("获取jvm内存信息")
    val clazz = jvmmxb.heapMemoryUsage.javaClass
    val items = mutableListOf<JvmInfo>()
    clazz.fields.forEach { f ->
        items.add(JvmInfo().apply {
            tags["job"] = "jvm_memory"
            tags["area"] = "heap"
            tags["target"] = "jvm_memory_bytes_" + f.name
            content = try {f.get(jvmmxb.heapMemoryUsage).toString()} catch (e : Exception) { e.stackTraceToString() }
        })
     }
    val nonHeapClazz = jvmmxb.nonHeapMemoryUsage.javaClass
    nonHeapClazz.fields.forEach { f ->
        items.add(JvmInfo().apply {
            tags["job"] = "jvm_memory"
            tags["area"] = "nonheap"
            tags["target"] = "jvm_memory_bytes_" + f.name
            content = try {f.get(jvmmxb.nonHeapMemoryUsage).toString()} catch (e : Exception) { e.stackTraceToString() }
        })
    }

    val memoryPoolMXBeanClazzFields = MemoryPoolMXBean::class.java.fields
    ManagementFactory.getMemoryPoolMXBeans().forEach { mxb ->
        memoryPoolMXBeanClazzFields.forEach { f ->
            items.add(JvmInfo().apply {
                tags["job"] = "jvm_memory"
                tags["pool"] = mxb.name
                tags["target"] = "jvm_memory_pool_bytes_" + f.name
                content = try { f.get(mxb).toString()} catch (e : Exception){ e.stackTraceToString() }
            })
        }
    }

    val gcFields = GarbageCollectorMXBean::class.java.fields
    ManagementFactory.getGarbageCollectorMXBeans().forEach { mxb ->
        gcFields.forEach { f ->
            items.add(JvmInfo().apply {
                tags["job"] = "jvm_memory"
                tags["gc"] = mxb.name
                tags["target"] = "jvm_gc_collection_seconds" + f.name
                content = try { f.get(mxb).toString()} catch (e : Exception) {e.stackTraceToString()}
            })
        }
    }
    val threadFields = ThreadMXBean::class.java
    val threadMxBean = ManagementFactory.getThreadMXBean()
    threadFields.fields.forEach { f ->
        items.add(JvmInfo().apply {
            tags["job"] = "jvm_memory"
            tags["state"] = f.name
            tags["target"] = "jvm_threads_state"
            content = try {f.get(threadMxBean).toString()} catch (e : Exception) {e.stackTraceToString()}
        })
    }
    val classLoadingMXBean = ManagementFactory.getClassLoadingMXBean()
    classLoadingMXBean.javaClass.fields.forEach { f ->
        items.add(JvmInfo().apply {
            tags["job"] = "jvm_memory"
            tags["target"] = "jvm_classes_" + f.name
            content = try {f.get(classLoadingMXBean).toString()} catch (e : Exception) {e.stackTraceToString()}
        })
    }

    blockingQueue.addAll(items)
}

class JvmInfo : LogModel()