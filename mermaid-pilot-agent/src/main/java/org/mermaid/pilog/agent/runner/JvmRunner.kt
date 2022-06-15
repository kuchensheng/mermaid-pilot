package org.mermaid.pilog.agent.runner

import org.mermaid.pilog.agent.plugin.factory.logger
import org.mermaid.pilog.agent.plugin.jvm.memory
import org.mermaid.pilog.agent.plugin.jvm.printMemoryInfo
import kotlin.concurrent.fixedRateTimer

/**
 * <p>
 * TODO
 * </p>
 *
 * @author Vector.Ku（kucs@isyscore.com）
 * @since 2022/5/26 14:48 星期四
 */
object JvmRunner : Runner {
    init {
        runner()
    }

    override fun runner() {
        logger.info("初始化JVMAdvice，每秒打印一次")
        fixedRateTimer("get memory and gc info",false,30000,10000) {
            logger.info("收集OS信息和jvm信息")
            try {
                memory()
            } catch (e : Exception) {
                logger.info("收集OS信息异常，$e")
            }
            printMemoryInfo()
        }
    }
}