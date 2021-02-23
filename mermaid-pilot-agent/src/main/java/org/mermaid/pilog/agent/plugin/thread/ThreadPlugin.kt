package org.mermaid.pilog.agent.plugin.thread

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.ThreadAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinTask

/**
 * description: 线程监控插件
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2010:35
 * @version 1.0
 */
object ThreadPlugin : IPlugin {
    override fun getName(): String = "thread"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object: InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.any<TypeDescription>()
                .and(ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named("java.util.concurrent.ExecutorService"))
                        .or(ElementMatchers.hasSuperType(ElementMatchers.named("java.util.concurrent.CompletionService"))))
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.named<MethodDescription>("submit")
                        .or(ElementMatchers.named("execute"))
                        .or(ElementMatchers.named("schedule"))
                        .or(ElementMatchers.named("scheduleAtFixedRate"))
                        .or(ElementMatchers.named("scheduleWithFixedDelay"))
                        .or(ElementMatchers.named("invoke")))
                .and(ElementMatchers.takesArgument<MethodDescription>(0,Runnable::class.java)
                        .or(ElementMatchers.takesArgument(0,Callable::class.java))
                        .or(ElementMatchers.takesArgument(0,ForkJoinTask::class.java)))
    })

    override fun interceptorAdviceClass(): Class<*>  = ThreadAdvice::class.java
}