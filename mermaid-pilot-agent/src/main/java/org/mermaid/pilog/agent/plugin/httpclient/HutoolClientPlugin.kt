package org.mermaid.pilog.agent.plugin.httpclient

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.matcher.ElementMatchers.*
import org.mermaid.pilog.agent.advice.HutoolClientAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/413:56
 * @version 1.0
 */
object HutoolClientPlugin : IPlugin{
    override fun getName(): String = "HutoolClientPlugin"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = not(isInterface<TypeDescription>()
                .and(isAbstract()))
                .and(named<TypeDescription>("cn.hutool.http.HttpRequest")
                        .or(hasSuperType(named("cn.hutool.http.HttpRequest"))))
        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = isMethod<MethodDescription>()
                .and(isPublic())
                .and(not(isConstructor()))
                .and(not(isAbstract()))
                .and(named("execute"))
    })

    override fun interceptorAdviceClass(): Class<*>  = HutoolClientAdvice::class.java
}