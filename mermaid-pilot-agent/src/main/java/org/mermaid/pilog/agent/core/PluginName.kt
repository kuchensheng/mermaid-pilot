package org.mermaid.pilog.agent.core

import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.httpclient.*
import org.mermaid.pilog.agent.plugin.jdbc.JdbcPlugin
import org.mermaid.pilog.agent.plugin.loki.LogBackPlugin
import org.mermaid.pilog.agent.plugin.process.ProcessPlugin
import org.mermaid.pilog.agent.plugin.redis.RedisPlugin
import org.mermaid.pilog.agent.plugin.servlet.ServletPlugin
import org.mermaid.pilog.agent.plugin.servlet.SpringPlugin

/**
 * description: 插件名枚举
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:46
 * @version 1.0
 */
enum class PluginName {
//    JVMPLUGIN("jvm",JvmPlugin),
    SERVLET("servlet",ServletPlugin),
//    PROCESS("process",ProcessPlugin),
    OKHTTP3PLUGIN("okhttp3", OkHttpClient3xPlugin),
//    THREADPLUGIN("thread",ThreadPlugin),
    JDBC("jdbc",JdbcPlugin),
    REDIS("redis",RedisPlugin),
//    JDKHTTPCLIENTPLUGIN("jdkulconnection", JdkHttpURLConnectionPlugin)
//    LOG4J("log4j",LogBackPlugin)
    SPRING("spring",SpringPlugin)
    ;

    var code: String
    var instance: IPlugin

    constructor(code:String,instance: IPlugin) {
        this.instance = instance
        this.code = code
    }
}