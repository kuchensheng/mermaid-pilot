package org.mermaid.pilog.agent

import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.asm.Advice
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.utility.JavaModule
import org.mermaid.pilog.agent.trace.PilogAdvice
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
            val transformer  = AgentBuilder.Transformer { builder, _, _, _ ->
                return@Transformer builder.visit(
                        Advice.to(PilogAdvice::class.java)
                                .on(ElementMatchers.isMethod<MethodDescription>()
                                        .and(ElementMatchers.any<Any>())
                                        .and(ElementMatchers.not(ElementMatchers.nameStartsWith("main")))))
            }


            val listener = object : AgentBuilder.Listener {
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
                    //TODO("Not yet implemented")
                }

                override fun onComplete(p0: String?, p1: ClassLoader?, p2: JavaModule?, p3: Boolean) {
                    //("Not yet implemented")
                }
            }

            AgentBuilder.Default().type(ElementMatchers.any())
                    .transform(transformer)
                    .with(listener)
                    .installOn(inst)
        }

        @JvmStatic
        fun premain(args: String?) {

        }
    }
}