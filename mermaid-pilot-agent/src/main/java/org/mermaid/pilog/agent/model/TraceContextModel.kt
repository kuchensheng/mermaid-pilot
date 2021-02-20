package org.mermaid.pilog.agent.model

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:31
 * @version 1.0
 */
class TraceContextModel {
    var traceId: String? = null
    var spanId: String? = null


    fun copy() = TraceContextModel().apply {
        this.traceId = traceId
        this.spanId = spanId
    }
}