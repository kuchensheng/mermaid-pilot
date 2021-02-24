package org.mermaid.pilog.agent.plugin.springcloud.gateway

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.SpringCloudGatewayFilterAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2415:34
 * @version 1.0
 */
object FilterPlugin : IPlugin {
    override fun getName(): String = "spring.cloud.gateway.filter"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object : InterceptPoint {
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named<TypeDescription>("org.springframework.cloud.gateway.filter.GlobalFilter"))
                .and(ElementMatchers.nameStartsWith("com.isyscore.os"))
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.named<MethodDescription>("filter"))
    })

    override fun interceptorAdviceClass(): Class<*> = SpringCloudGatewayFilterAdvice::class.java
}