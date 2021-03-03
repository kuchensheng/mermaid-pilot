package org.mermaid.pilog.agent

import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.asm.Advice
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.utility.JavaModule
import org.mermaid.pilog.agent.common.blockingQueue
import org.mermaid.pilog.agent.common.consume
import org.mermaid.pilog.agent.common.report
import org.mermaid.pilog.agent.handler.loadHandler
import org.mermaid.pilog.agent.intercept.HttpClientIntercepter
import org.mermaid.pilog.agent.plugin.factory.loadPlugin
import org.mermaid.pilog.agent.plugin.factory.pluginGroup
import java.lang.instrument.Instrumentation
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * description: vm参数中添加：-javaagent:${user.home}\mermaid-pilot-agent\target\mermaid-pilot-agent-1.0-jar-with-dependencies.jar
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
            initialize()
            addPlugin().run { addIntercepter(this) }.run { installOn(inst) }
        }

        @JvmStatic
        fun agentmain(args: String?, inst: Instrumentation) {
            initialize()
            addPlugin().run { addIntercepter(this) }.run { installOn(inst) }
        }

        /**
         * 设置全局不匹配的类规则
         */
        private fun notMatcher(): ElementMatcher.Junction<TypeDescription>  = ElementMatchers.not(ElementMatchers.nameContains("intellij"))

        /**
         * 设置全量过滤器匹配规则
         */
        private fun intercepteMatcher() : ElementMatcher.Junction<TypeDescription> = ElementMatchers.named("cn.hutool.http.HttpRequest")//ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named("org.springframework.cloud.gateway.filter.GlobalFilter"))
//                .or(ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named("org.springframework.cloud.gateway.filter.GlobalFilter"))
//                        .and(ElementMatchers.not(ElementMatchers.isInterface()))
//                        .and(ElementMatchers.not(ElementMatchers.isAbstract())))

        /**
         * 添加插件
         */
        private fun addPlugin() : AgentBuilder {
            var agentBuilder : AgentBuilder = AgentBuilder.Default().with(builderListener()).disableClassFormatChanges()
                    .ignore(ElementMatchers.none<TypeDescription>().and(ElementMatchers.nameStartsWith<TypeDescription>("main")))
            pluginGroup.forEach { p -> p.buildInterceptPoint().forEach { agentBuilder = agentBuilder.type(notMatcher().and(it.buildTypesMatcher())).transform { builder, _, _, _ -> builder.visit(Advice.to(p.interceptorAdviceClass()).on(ElementMatchers.not(ElementMatchers.isConstructor()).and(it.buildMethodsMatcher()))) } } }
            return agentBuilder
        }

        /**
         * 添加拦截器
         */
        private fun addIntercepter(agentBuilder: AgentBuilder) : AgentBuilder = agentBuilder.type(intercepteMatcher()).transform { builder, _, _, _ -> builder.method(ElementMatchers.isMethod<MethodDescription>().and(ElementMatchers.named("execute")))
                    .intercept(MethodDelegation.to(HttpClientIntercepter::class.java)).also { println("添加拦截器") }
        }

        /**
         * 构建监听器
         */
        private fun builderListener(): AgentBuilder.Listener? = object : AgentBuilder.Listener {
            override fun onDiscovery(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                //TODO("Not yet implemented")
            }

            override fun onTransformation(typeDescription: TypeDescription?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean, p4: DynamicType?) {
                println("onTransformation:$typeDescription,dynamicType:$p4")
            }

            override fun onIgnored(p0: TypeDescription?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                //TODO("Not yet implemented")
            }

            override fun onError(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean, p4: Throwable?) {
                println("方法执行异常,class is $p0,classLoader is $p1,Throwable:\n $p4")
            }

            override fun onComplete(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                //("Not yet implemented")
            }
        }

        /**
         * 初始化信息
         */
        private fun initialize() {
            println("基于javaagent链路跟踪PIVOT信息收集器")
            println("=================================================================")
            loadPlugin()
            loadHandler()
            ThreadPoolExecutor(1,4,0,TimeUnit.SECONDS,LinkedBlockingQueue()).execute { while (true) if (blockingQueue.isEmpty()) Thread.sleep(100) else consume()?.run { report(this) } }
        }
    }
}