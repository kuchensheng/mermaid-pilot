package org.mermaid.pilog.agent.report

import org.mermaid.pilog.agent.model.Span

/**
 * description: 消息上发
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2410:39
 * @version 1.0
 */
interface IReporter {

    fun report(span: Span) : Int?

    fun report(list: List<Span>) : Int?

}

fun getReporter(className: String?) : IReporter? = null