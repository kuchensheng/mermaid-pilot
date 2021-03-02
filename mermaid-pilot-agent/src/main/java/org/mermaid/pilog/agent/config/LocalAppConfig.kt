package org.mermaid.pilog.agent.config

import org.mermaid.pilog.agent.plugin.factory.logger
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/215:36
 * @version 1.0
 */
object LocalAppConfig : ApplicationContextAware {
    private var applicationContext : ApplicationContext? = null
    override fun setApplicationContext(p0: ApplicationContext) {
        this.applicationContext = p0
    }
    fun getAppName() : String? {
        logger.info("获取应用名称,applicationContext is null?[${applicationContext?:false}],applicationContext.applicationName:${applicationContext?.applicationName},applicationName:${applicationContext?.environment?.getProperty("spring.application.name")}")
        return (applicationContext?.environment?.getProperty("spring.application.name")) ?: applicationContext?.applicationName
    }
}
//@Configuration
//open class LocalAppConfig() : ApplicationContextAware {
//    private lateinit var applicationContext : ApplicationContext
//
//    override fun setApplicationContext(applicationContext: ApplicationContext) {
//        this.applicationContext = applicationContext
//    }
//
//
//    fun getAppName() : String = applicationContext.environment.getProperty("spring.application.name")
//}

