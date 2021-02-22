package org.mermaid.pilog.agent

import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.asm.Advice
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.utility.JavaModule

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2012:45
 * @version 1.0
 */
class PilotTransformer : AgentBuilder.Transformer {
    var adviceClass : Class<*>? = null
    var elementMatcher : ElementMatcher<MethodDescription>? = null

    @JvmOverloads
    constructor(adviceClass: Class<*>,elementMatcher : ElementMatcher<MethodDescription>?) {
        this.adviceClass = adviceClass
        this.elementMatcher = elementMatcher
    }
    override fun transform(builder: DynamicType.Builder<*>, typeDescription: TypeDescription?, classLoader: ClassLoader?, module: JavaModule?): DynamicType.Builder<*> {
        return builder.visit(Advice.to(adviceClass).on(ElementMatchers.not(ElementMatchers.isConstructor()).and(elementMatcher)))
    }
}