package org.mermaid.pilog.agent.plugin.loki

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.Log4jAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

object Log4JPlugin : IPlugin {
    override fun getName(): String = "log4j"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.hasSuperType(ElementMatchers.named("ch.qos.logback.core.encoder.LayoutWrappingEncoder"))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
            .and(ElementMatchers.named("convertToBytes"))
    })

    override fun interceptorAdviceClass(): Class<*> = Log4jAdvice::class.java
}