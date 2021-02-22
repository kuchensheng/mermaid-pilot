package org.mermaid.pilog.agent.core

import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.jvm.JvmPlugin
import org.mermaid.pilog.agent.plugin.servlet.ServletPlugin
import org.mermaid.pilog.agent.plugin.springweb.SpringWebPlugin

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
    SPRINGWEB("springweb",SpringWebPlugin)
    ;

    var code: String
    var instance: IPlugin

    constructor(code:String,instance: IPlugin) {
        this.instance = instance
        this.code = code
    }
}