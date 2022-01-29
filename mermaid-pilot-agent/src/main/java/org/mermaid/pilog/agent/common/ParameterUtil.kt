package org.mermaid.pilog.agent.common

import java.lang.reflect.Method

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/3/211:07
 * @version 1.0
 */

/**
 * 方法参数信息收集
 */
fun collectParameters(method: Method, args: Array<*>?) : MutableMap<String,Any?>  = (args?: arrayOf<Any>()).let { method.parameters.mapIndexed { index, parameter -> parameter.name to it[index] }.toMap().toMutableMap() }


