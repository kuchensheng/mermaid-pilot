package org.mermaid.pilog.agent.plugin.springweb

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.SpringWebAdvice
import org.mermaid.pilog.agent.core.PluginName
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2010:35
 * @version 1.0
 */
class SpringWebPlugin : IPlugin {
    override fun getName(): String? = PluginName.SPRINGWEB.code
    val restCollection: Class<out Annotation> = Class.forName("org.springframework.web.bind.annotation.RestController") as Class<out Annotation>
    val controller : Class<out Annotation> = Class.forName("org.springframework.stereotype.Controller") as Class<out Annotation>
    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription>? = ElementMatchers.isAnnotatedWith<TypeDescription>(restCollection)
                .or(ElementMatchers.isAnnotatedWith<TypeDescription>(controller))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription>? = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.isPublic<MethodDescription>())
    })

    override fun interceptorAdviceClass(): Class<*>  {
        println("获取通知类SpringWebAdvice")
        return SpringWebAdvice::class.java
    }
}