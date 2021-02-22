package org.mermaid.pilog.agent.plugin.factory

import org.mermaid.pilog.agent.core.PluginName
import org.mermaid.pilog.agent.plugin.IPlugin
import org.mermaid.pilog.agent.plugin.jvm.JvmPlugin
import org.mermaid.pilog.agent.plugin.servlet.ServletPlugin
import org.mermaid.pilog.agent.plugin.springweb.SpringWebPlugin
import java.util.logging.Logger

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1910:06
 * @version 1.0
 */
val pluginGroup = arrayListOf<IPlugin>()
val logger: Logger = Logger.getLogger("PluginFactory")
fun loadPlugin() = PluginName.values().forEach {
    logger.info("加载${it.code}")
    pluginGroup.add(it.instance)
}