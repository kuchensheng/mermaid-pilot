package org.mermaid.pilog.agent.core

import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.httpclient.HttpClient3Plugin
import org.mermaid.pilog.agent.plugin.httpclient.HttpClient4Plugin
import org.mermaid.pilog.agent.plugin.httpclient.OkHttpClient3xPlugin
import org.mermaid.pilog.agent.plugin.jvm.JvmPlugin
import org.mermaid.pilog.agent.plugin.process.ProcessPlugin
import org.mermaid.pilog.agent.plugin.servlet.ServletPlugin
import org.mermaid.pilog.agent.plugin.springweb.SpringWebPlugin
import org.mermaid.pilog.agent.plugin.thread.ThreadPlugin

/**
 * description: TODO
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
//    THREADPLUGIN("thread",ThreadPlugin),
    SPRINGWEB("springweb",SpringWebPlugin)
    ;

    var code: String
    var instance: IPlugin

    constructor(code:String,instance: IPlugin) {
        this.instance = instance
        this.code = code
    }
}