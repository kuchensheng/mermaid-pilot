package org.mermaid.pilog.agent.plugin.httpclient

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.HttpClientAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: Apache HTTPClient3 插件
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2216:51
 * @version 1.0
 */
object HttpClient3Plugin : IPlugin {
    override fun getName(): String = "httpclient3"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint {
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.hasSuperType(ElementMatchers.named<TypeDescription>("org.apache.commons.httpclient.HttpClient"))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.named<MethodDescription>("executeMethod"))
    })

    override fun interceptorAdviceClass(): Class<*>  = HttpClientAdvice::class.java
}