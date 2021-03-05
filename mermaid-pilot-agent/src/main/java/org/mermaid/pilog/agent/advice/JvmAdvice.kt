package org.mermaid.pilog.agent.advice

import net.bytebuddy.asm.Advice
import org.mermaid.pilog.agent.plugin.jvm.printGCInfo
import org.mermaid.pilog.agent.plugin.jvm.printMemoryInfo

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:49
 * @version 1.0
 */
object JvmAdvice {
    @JvmStatic
    @Advice.OnMethodExit
    fun exit() {
        printMemoryInfo()
        printGCInfo()
    }
}