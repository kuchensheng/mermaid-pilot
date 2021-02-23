package org.mermaid.pilog.agent.model

import org.mermaid.pilog.agent.common.generateTraceId

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:31
 * @version 1.0
 */
class TraceContextModel {
    var traceId: String = ""
    var spanId: String? = null

    constructor() {this.traceId = generateTraceId()}
    constructor(traceId: String) {this.traceId = traceId}

    fun copy() = TraceContextModel().apply {
        this.traceId = traceId
        this.spanId = spanId
    }
}