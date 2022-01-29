package org.mermaid.pilog.agent.plugin.loki

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.LogBackAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

object LogBackPlugin : IPlugin {
    override fun getName(): String = "log4j"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.hasSuperType(ElementMatchers.named("ch.qos.logback.core.encoder.LayoutWrappingEncoder"))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
            .and(ElementMatchers.named("convertToBytes"))
    })

    override fun interceptorAdviceClass(): Class<*> = LogBackAdvice::class.java
}