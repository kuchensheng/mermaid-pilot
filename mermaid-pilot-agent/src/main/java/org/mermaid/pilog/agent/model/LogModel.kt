package org.mermaid.pilog.agent.model

/**
 * <p>
 * TODO
 * </p>
 *
 * @author 酷达舒（kucs@isyscore.com）
 * @since 2022/1/29 14:59 星期六
 */
abstract class LogModel {
    val tags = mutableMapOf<String,String>()
    var content : String = ""

    override fun toString(): String {
        return content
    }
}