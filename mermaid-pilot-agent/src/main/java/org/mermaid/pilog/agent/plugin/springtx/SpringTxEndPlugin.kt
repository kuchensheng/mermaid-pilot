package org.mermaid.pilog.agent.plugin.springtx

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.SpringTxEndAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/249:46
 * @version 1.0
 */
object SpringTxEndPlugin : IPlugin {
    override fun getName(): String = "springTxEnd"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object : InterceptPoint {
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.named("org.springframework.jdbc.datasource.DataSourceTransactionManager")

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.named("doCleanupAfterCompletion"))
    })

    override fun interceptorAdviceClass(): Class<*> = SpringTxEndAdvice::class.java
}