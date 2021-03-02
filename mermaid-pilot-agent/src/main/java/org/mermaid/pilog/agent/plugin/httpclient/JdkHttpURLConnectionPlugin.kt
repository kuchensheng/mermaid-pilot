package org.mermaid.pilog.agent.plugin.httpclient

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers
import org.mermaid.pilog.agent.advice.JdkHttpClientAdvice
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.InterceptPoint

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/210:03
 * @version 1.0
 */
object JdkHttpURLConnectionPlugin : IPlugin {
    override fun getName(): String = "HttpURLConnectionPlugin"

    override fun buildInterceptPoint(): Array<InterceptPoint> = arrayOf(object : InterceptPoint{
        override fun buildTypesMatcher(): ElementMatcher<TypeDescription> = ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named("java.net.URLConnection"))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.named<TypeDescription>("sun.net.www.protocol.http.HttpURLConnection").or(ElementMatchers.hasSuperType<TypeDescription>(ElementMatchers.named("sun.net.www.protocol.http.HttpURLConnection"))))

        override fun buildMethodsMatcher(): ElementMatcher<MethodDescription> = ElementMatchers.isMethod<MethodDescription>()
                .and(ElementMatchers.named<MethodDescription>("connect")
                        //获取的输出流对象方法
                        .or(ElementMatchers.named<MethodDescription>("getOutputStream"))
                        .or(ElementMatchers.named<MethodDescription>("setRequestMethod"))
                        .or(ElementMatchers.named<MethodDescription>("setRequestProperty"))
                        .or(ElementMatchers.named<MethodDescription>("addRequestProperty")))

    })

    override fun interceptorAdviceClass(): Class<*> = JdkHttpClientAdvice::class.java
}