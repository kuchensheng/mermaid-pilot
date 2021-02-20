package org.mermaid.pilog.agent.plugin

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:34
 * @version 1.0
 */
interface IPlugin {
    fun getName():String?

    fun buildInterceptPoint() : Array<InterceptPoint>

    /**
     * 拦截器类
     */
    fun interceptorAdviceClass() : Class<*>
}