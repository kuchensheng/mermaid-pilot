package org.mermaid.pilog.agent.plugin.process

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.ProcessAdvice
import org.mermaid.pilog.agent.common.buildMethodsMatcher_
import org.mermaid.pilog.agent.common.buildTypesMatcher_
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: 类内部方法处理插件
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2217:59
 * @version 1.0
 */
val serviceAnnotation : Class<out Annotation> = Class.forName("org.springframework.stereotype.Service") as Class<out Annotation>
object ProcessPlugin : IPlugin {
    override fun getName(): String = "process"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.isAnnotatedWith(serviceAnnotation)

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription>? = buildMethodsMatcher_(null,null)
    })

    override fun interceptorAdviceClass(): Class<*>  = ProcessAdvice::class.java
}