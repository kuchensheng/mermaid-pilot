package org.mermaid.pilog.agent

import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.asm.Advice
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.utility.JavaModule
import org.mermaid.pilog.agent.advice.SpringWebAdvice
import org.mermaid.pilog.agent.handler.loadHandler
import org.mermaid.pilog.agent.plugin.factory.loadPlugin
import org.mermaid.pilog.agent.plugin.factory.pluginGroup
import java.lang.instrument.Instrumentation

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1011:40
 * @version 1.0
 */
class PilotAgent {
    companion object {
        @JvmStatic
        fun premain(args: String?, inst: Instrumentation) {
            println("基于javaagent链路跟踪PIVOT信息收集器")
            println("=================================================================")
            val listener = builderListener()
            loadPlugin()
            loadHandler()
            var agentBuilder : AgentBuilder = AgentBuilder.Default().with(listener).disableClassFormatChanges()
                    .ignore(ElementMatchers.none<TypeDescription>().and(ElementMatchers.nameStartsWith<TypeDescription>("main")))
            pluginGroup.forEach { p -> p.buildInterceptPoint().forEach { agentBuilder = agentBuilder.type(notMatcher().and(it.buildTypesMatcher())).transform { builder, _, _, _ -> builder.visit(Advice.to(p.interceptorAdviceClass()).on(ElementMatchers.not(ElementMatchers.isConstructor()).and(it.buildMethodsMatcher()))) } } }
            agentBuilder.installOn(inst)

        }

        private fun notMatcher(): ElementMatcher.Junction<TypeDescription>  = ElementMatchers.not(ElementMatchers.nameContains("intellij"))

        private fun builderListener(): AgentBuilder.Listener? = object : AgentBuilder.Listener {
            override fun onDiscovery(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                //TODO("Not yet implemented")
            }

            override fun onTransformation(typeDescription: TypeDescription?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean, p4: DynamicType?) {
                println("onTransformation:$typeDescription")
            }

            override fun onIgnored(p0: TypeDescription?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                //TODO("Not yet implemented")
            }

            override fun onError(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean, p4: Throwable?) {
                println("方法执行异常,p0 is $p0,classLoader is $p1,Throwable is $p4")
            }

            override fun onComplete(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                //("Not yet implemented")
            }
        }
    }
}