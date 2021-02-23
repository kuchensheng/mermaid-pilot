package org.mermaid.pilog.agent.plugin.jdbc

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.PreparedStatementParamAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint
import java.sql.PreparedStatement

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2315:30
 * @version 1.0
 */

object JdbcPlugin : IPlugin {

    override fun getName(): String = "jdbc"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.isSubTypeOf<TypeDescription>(PreparedStatement::class.java)
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("com.sun")))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.nameStartsWith<MethodDescription>("execute")
                        .or(ElementMatchers.nameStartsWith<MethodDescription>("set")
                                .and(ElementMatchers.takesArgument<MethodDescription>(0,Int.javaClass))))
                .and(ElementMatchers.isPublic())
                .and(ElementMatchers.not(ElementMatchers.named<MethodDescription>("executeInternal")
                        .or(ElementMatchers.named("setInternal"))))
    })

    override fun interceptorAdviceClass(): Class<*>  = PreparedStatementParamAdvice::class.java
}