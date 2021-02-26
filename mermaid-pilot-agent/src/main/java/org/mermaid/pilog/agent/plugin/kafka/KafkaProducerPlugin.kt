package org.mermaid.pilog.agent.plugin.kafka

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.KafkaAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: org.apache.kafka.clients.producer.KafkaProducer 监控
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2619:02
 * @version 1.0
 */

object KafkaProducerPlugin : IPlugin{
    override fun getName(): String = "kafka"

    override fun buildInterceptPoint(): Array<InterceptPoint>  = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.named<TypeDescription>("org.apache.kafka.clients.producer.KafkaProducer")
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))


        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.isPublic())
                .and(ElementMatchers.named("send"))
    })

    override fun interceptorAdviceClass(): Class<*> = KafkaAdvice::class.java
}