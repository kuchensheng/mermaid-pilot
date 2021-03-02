package org.mermaid.pilog.agent.config

import org.mermaid.pilog.agent.plugin.factory.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/215:36
 * @version 1.0
 */
@Configuration
open class LocalAppConfig {
    @Autowired
    internal lateinit var env : Environment

    fun getAppName() : String = env.getProperty("spring.application.name")
}

