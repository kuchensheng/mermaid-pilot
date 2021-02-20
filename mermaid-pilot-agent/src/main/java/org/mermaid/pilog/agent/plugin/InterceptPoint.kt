package org.mermaid.pilog.agent.plugin

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher

/**
 * description: 拦截点接口
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:36
 * @version 1.0
 */
interface InterceptPoint {

    /**
     * 类匹配规则
     * @return
     */
    fun buildTypesMatcher() : ElementMatcher<TypeDescription>?

    /**
     * 方法匹配规则
     * @return
     */
    fun buildMethodsMatcher() : ElementMatcher<MethodDescription>?
}