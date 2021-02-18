package org.mermaid.pilog.agent.trace

import java.time.LocalDateTime

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/189:47
 * @version 1.0
 */
class Span(traceId: String) {
    lateinit var traceId: String
    lateinit var spanId: String
    var parentId:String? = null
    var startTime: LocalDateTime? = LocalDateTime.now()
    var endTime: LocalDateTime? = null
    var parameterInfo: Map<String,Any?>? = null
    var result:Map<String,Any?>? = null
    var className: String? = null
    var methodName: String = ""
}