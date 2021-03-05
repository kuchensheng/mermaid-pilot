package org.mermaid.pilog.agent.intercept

import cn.hutool.http.HttpRequest
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers.named
import net.bytebuddy.matcher.ElementMatchers.takesArguments

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/420:55
 * @version 1.0
 */
object HutoolHttpRequestSubClass {
    fun generateHutoolHttpRequestSubClass() : Class<*>  = ByteBuddy()
            .subclass(HttpRequest::class.java)
            //.name("hutoolHttpRequestSubClass") //NamingStrategy 未命名 默认会随机生成类名
            .make()
            .load(javaClass.classLoader, ClassLoadingStrategy.Default.WRAPPER).loaded

            .also { println("生成字类:${it}") }
    fun interceptHutoolHttpRequestSubClass() : Class<*> = ByteBuddy()
            .subclass(HttpRequest::class.java)
            .method(named<MethodDescription>("execute").and(takesArguments(0)))
            .intercept(MethodDelegation.to(HttpClientIntercepter::class.java))
            .make()
            .load(HttpRequest::class.java.classLoader,ClassLoadingStrategy.Default.WRAPPER)
            .loaded
}
