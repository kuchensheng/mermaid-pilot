package org.mermaid.pilog.agent.trace

import java.util.*

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1012:08
 * @version 1.0
 */
class TraceManager {

    val trace = ThreadLocal<Stack<String>>()

    fun createSpan(): String = ""
}