package org.mermaid.pilog.agent.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mermaid.pilog.agent.advice.LogInfo
import java.io.RandomAccessFile
import java.nio.file.*
import java.time.LocalDateTime
import java.time.ZoneOffset

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
                "log.path" -> logFileWatcher(it[1])
            }
        }
    }
}

private fun logFileWatcher(dir : String) {
    println("监听日志文件目录:$dir")
    val watchService = FileSystems.getDefault().newWatchService()
    Paths.get(dir).register(watchService, arrayOf(StandardWatchEventKinds.ENTRY_MODIFY))
    var lastPosition = 0L
    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            //文件监听,获取不到事件则等待
            val take = watchService.take()
            //获取当前时间戳
            val now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()
            val event = take.pollEvents()[0]
            //读取监听Path
            val path = com.sun.jmx.mbeanserver.Util.cast<Path>(event.context())
            println("path=$path")
            //只关注目标文件
            val fullPath = Paths.get(dir, path.toString())
            println("fullPath=$fullPath")
            //目标文件最新更新事件
            val lastModifiedTime = Files.getLastModifiedTime(fullPath).toMillis()
            //更新事件超过1s钟的文件内容则放弃读取
            if (now - lastModifiedTime > 1000) {
                lastPosition = Files.size(fullPath)
                take.reset()
                continue
            }
            println("读取文件内容path=$fullPath,lastPosition=$lastPosition")
            try {
                val accessFile = RandomAccessFile(fullPath.toFile(), "r")
                accessFile.seek(lastPosition)
                val buffer = ByteArray(65535)
                while (accessFile.read(buffer) <= 0) {
                    break;
                }
                buffer.toString().split("\n").forEach {
                    val logInfo = LogInfo().apply {
                        tags["kucs"] = "watcher"
                        content = it
                    }
                    blockingQueue.add(logInfo)
                }
            } catch (e: Exception) {
                e.stackTrace
            } finally {
                take.reset()
            }
        }
    }

//    val observer = FileAlterationObserver(dir)
//    val adaptor = object : FileAlterationListenerAdaptor() {
//        override fun onFileChange(file: File) {
//            super.onFileChange(file)
//        }
//    }
//    //注册监听器
//    observer.addListener(adaptor)
//    //启动监听
//    val monitor = FileAlterationMonitor().apply { addObserver(observer) }
//    //启动协程
//    CoroutineScope(Dispatchers.IO).launch {
//        monitor.start()
//    }

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