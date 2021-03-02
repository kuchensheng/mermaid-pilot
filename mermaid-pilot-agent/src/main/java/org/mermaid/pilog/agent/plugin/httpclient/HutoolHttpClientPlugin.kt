package org.mermaid.pilog.agent.plugin.httpclient

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.HttpClientAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: Hutool Http Client 调用监控
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/116:58
 * @version 1.0
 */
object HutoolHttpClientPlugin : IPlugin {
    override fun getName(): String?  = "Hutool"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.named<TypeDescription>("cn.hutool.http.HttpRequest")
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))


        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.isPublic())
                .and(ElementMatchers.named("execute"))
    })

    override fun interceptorAdviceClass(): Class<*> = HttpClientAdvice::class.java
}