package org.mermaid.pilog.agent.plugin.jvm

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.JvmAdvice
import org.mermaid.pilog.agent.core.PluginName
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint


/**
 * description: jvm插件
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:46
 * @version 1.0
 */
//object JvmPlugin : IPlugin {
//
//    override fun getName(): String? = PluginName.JVMPLUGIN.code
//
//    override fun buildInterceptPoint(): Array<InterceptPoint> {
//        return arrayOf(
//                object : InterceptPoint {
//                    override fun buildTypesMatcher(): ElementMatcher<TypeDescription> {
//                        return ElementMatchers.nameStartsWith<TypeDescription>("org.example")
//                    }
//
//                    override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> {
//                        return ElementMatchers.isMethod<MethodDescription>()
//                                .and(ElementMatchers.any<MethodDescription>())
//                                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("main")))
//                    }
//                }
//        )
//    }
//
//    override fun interceptorAdviceClass(): Class<*>  = JvmAdvice::class.java
//}