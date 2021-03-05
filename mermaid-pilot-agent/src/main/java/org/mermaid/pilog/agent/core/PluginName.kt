package org.mermaid.pilog.agent.core

import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.httpclient.*
import org.mermaid.pilog.agent.plugin.jdbc.JdbcPlugin
import org.mermaid.pilog.agent.plugin.jvm.JvmPlugin
import org.mermaid.pilog.agent.plugin.process.ProcessPlugin
import org.mermaid.pilog.agent.plugin.redis.RedisPlugin
import org.mermaid.pilog.agent.plugin.servlet.ServletPlugin
import org.mermaid.pilog.agent.plugin.springweb.SpringWebPlugin

/**
 * description: 插件名枚举
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1817:46
 * @version 1.0
 */
enum class PluginName {
    JVMPLUGIN("jvm",JvmPlugin),
    SERVLET("servlet",ServletPlugin),
    HTTP3PLUGIN("http3",HttpClient3Plugin),
    HTTP4PLUGIN("http4", HttpClient4Plugin),
    PROCESS("process",ProcessPlugin),
    OKHTTP3PLUGIN("okhttp3", OkHttpClient3xPlugin),
    HUTOOLPLUGIN("hutool",HutoolClientPlugin),
//    THREADPLUGIN("thread",ThreadPlugin),
    SPRINGWEB("springweb",SpringWebPlugin),
    JDBC("jdbc",JdbcPlugin),
    REDIS("redis",RedisPlugin),
//    JDKHTTPCLIENTPLUGIN("jdkulconnection", JdkHttpURLConnectionPlugin)
    ;

    var code: String
    var instance: IPlugin

    constructor(code:String,instance: IPlugin) {
        this.instance = instance
        this.code = code
    }
}