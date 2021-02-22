package org.mermaid.pilog.agent.plugin.servlet

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.ServletAdvice
import org.mermaid.pilog.agent.core.PluginName
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint
import org.mermaid.pilog.agent.plugin.springweb.controller
import org.mermaid.pilog.agent.plugin.springweb.restCollection

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1910:40
 * @version 1.0
 */
object ServletPlugin : IPlugin {
    val httpServletName = "javax.servlet.http.HttpServlet"
    override fun getName(): String = PluginName.SERVLET.code

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        //todo 缺少一步：排除不想拦截的servlet
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named<TypeDescription>(httpServletName)
                .and(ElementMatchers.not(ElementMatchers.isAnnotatedWith<TypeDescription>(restCollection).or(ElementMatchers.isAnnotatedWith(controller)))))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.takesArguments<MethodDescription>(2)
                        .and(ElementMatchers.takesArgument(0, ElementMatchers.named("javax.servlet.http.HttpServletRequest")))
                        .and(ElementMatchers.takesArgument(1, ElementMatchers.named("javax.servlet.http.HttpServletResponse")))
                        .and(ElementMatchers.nameStartsWith("do")))
    })

    override fun interceptorAdviceClass(): Class<*> = ServletAdvice::class.java
}