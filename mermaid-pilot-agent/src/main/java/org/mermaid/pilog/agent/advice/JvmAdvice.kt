package org.mermaid.pilog.agent.advice

import org.mermaid.pilog.agent.plugin.jvm.printMemoryInfo
import kotlin.concurrent.fixedRateTimer

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:49
 * @version 1.0
 */
object JvmAdvice {
    init {
        println("初始化JVMAdvice，每秒打印一次")
        fixedRateTimer("get memory and gc info",false,30000,1000) {
            printMemoryInfo()
        }
    }
}