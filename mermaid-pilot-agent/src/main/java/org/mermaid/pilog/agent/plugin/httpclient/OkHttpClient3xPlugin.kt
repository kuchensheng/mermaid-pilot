package org.mermaid.pilog.agent.plugin.httpclient

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.HttpClientAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: OkHttp HTTPClient3 插件
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2216:51
 * @version 1.0
 */
object OkHttpClient3xPlugin : IPlugin {
    override fun getName(): String = "okhttp3x"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription>?  = ElementMatchers.named("okhttp3.Request")

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isConstructor()
    })

    override fun interceptorAdviceClass(): Class<*>  = HttpClientAdvice::class.java
}