package org.mermaid.pilog.agent.plugin.servlet

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.SpringAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1910:40
 * @version 1.0
 */
val controllerAnnotation : Class<out Annotation> = Class.forName("org.springframework.web.bind.annotation.RestController") as Class<out Annotation>
object SpringPlugin : IPlugin {
    override fun getName(): String = "spring"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.isAnnotatedWith(controllerAnnotation)

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription>? = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.isPublic<MethodDescription>())
                .and(ElementMatchers.not(ElementMatchers.isConstructor()))
    })

    override fun interceptorAdviceClass(): Class<*>  = SpringAdvice::class.java
}