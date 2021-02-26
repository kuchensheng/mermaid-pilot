package org.mermaid.pilog.agent.plugin.redis

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.RedisAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: Redis操作拦截
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2618:42
 * @version 1.0
 */
object RedisPlugin : IPlugin{
    override fun getName(): String = "redis"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object :InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.named<TypeDescription>("org.springframework.data.redis.core.RedisTemplate")
                .or(ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named("org.springframework.data.redis.core.RedisTemplate")))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                .and(ElementMatchers.isPublic())
    })

    override fun interceptorAdviceClass(): Class<*> = RedisAdvice::class.java
}