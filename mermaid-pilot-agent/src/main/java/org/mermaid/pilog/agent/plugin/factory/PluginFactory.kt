package org.mermaid.pilog.agent.plugin.factory

import org.mermaid.pilog.agent.core.PluginName
import org.mermaid.pilog.agent.plugin.IPlugin
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
val logger = Logger.getLogger("PluginFactory")
fun loadPlugin() = PluginName.values().forEach {
    logger.info("加载${it.code}")
    pluginGroup.add(it.clazz.newInstance())
}