package org.mermaid.pilog.agent.common

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2219:22
 * @version 1.0
 */
private const val NAMED = "named"
private const val NAME_START = "nameStartsWith"
private const val NAME_END = "nameEndsWith"
private const val NAME_CONTAINS = "nameContains"
private const val NAME_MATCH = "nameMatches"
private const val HAS_SUPERTYPE = "hasSuperType"
private const val HAS_ANNOTATION = "hasAnnotation"
private const val IS_ANNOTATED_WITH = "isAnnotatedWith"
fun buildTypesMatcher_(includeMap: Map<String?, String?>?, excludeMap: Map<String?, String?>?): ElementMatcher.Junction<TypeDescription?>? {
    var matcher = ElementMatchers.not(ElementMatchers.nameStartsWith<TypeDescription>("org.mermaid.pilog."))
    if (!includeMap.isNullOrEmpty()) {
        var includeMatcher: ElementMatcher.Junction<TypeDescription>? = null
        includeMap[NAMED]?.run { split(",".toRegex()).toTypedArray().let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher)  ElementMatchers.named(it[n]) else includeMatcher!!.or(ElementMatchers.named(it[n])) } } }
        includeMap[NAME_START]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameStartsWith(it[n]) else includeMatcher!!.or(ElementMatchers.nameStartsWith(it[n])) } } }
        includeMap[NAME_END]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameEndsWith(it[n]) else includeMatcher!!.or(ElementMatchers.nameEndsWith(it[n])) } } }
        includeMap[NAME_CONTAINS]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameContains(it[n]) else includeMatcher!!.or(ElementMatchers.nameContains(it[n])) } } }
        includeMap[NAME_MATCH]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameMatches(it[n]) else includeMatcher!!.or(ElementMatchers.nameMatches(it[n])) } } }
        includeMap[HAS_SUPERTYPE]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.hasSuperType(ElementMatchers.named(it[n])) else includeMatcher!!.or(ElementMatchers.hasSuperType(ElementMatchers.named(it[n]))) } } }
        includeMap[HAS_ANNOTATION]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.hasAnnotation(ElementMatchers.annotationType(ElementMatchers.named(it[n]))) else includeMatcher!!.or(ElementMatchers.hasAnnotation(ElementMatchers.annotationType(ElementMatchers.named(it[n])))) } } }
        includeMatcher?.run { matcher.and(this) }
    }
    if (!excludeMap.isNullOrEmpty()) {
        excludeMap[NAMED]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.named(it[n]))) } } }
        excludeMap[NAME_START]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(it[n]))) } } }
        excludeMap[NAME_END]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameEndsWith(it[n]))) } } }
        excludeMap[NAME_CONTAINS]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameContains(it[n]))) } } }
        excludeMap[NAME_MATCH]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameMatches(it[n]))) } } }
        excludeMap[HAS_SUPERTYPE]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.hasSuperType(ElementMatchers.named(it[n])))) } } }
        excludeMap[HAS_ANNOTATION]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.hasAnnotation(ElementMatchers.annotationType(ElementMatchers.named(it[n]))))) } } }
    }
    
    return matcher
}

fun buildMethodsMatcher_(includeMap: Map<String?, String?>?, excludeMap: Map<String?, String?>?): ElementMatcher.Junction<MethodDescription>? {
    var matcher = ElementMatchers.isMethod<MethodDescription?>()
    if (!includeMap.isNullOrEmpty()) {
        var includeMatcher: ElementMatcher.Junction<MethodDescription>? = null
        includeMap[NAMED]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.named(it[n]) else includeMatcher!!.or(ElementMatchers.named(it[n])) } } }
        includeMap[NAME_START]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameStartsWith(it[n]) else includeMatcher!!.or(ElementMatchers.nameStartsWith(it[n])) } } }
        includeMap[NAME_END]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameEndsWith(it[n]) else includeMatcher!!.or(ElementMatchers.nameEndsWith(it[n])) } } }
        includeMap[NAME_CONTAINS]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameContains(it[n]) else includeMatcher!!.or(ElementMatchers.nameContains(it[n])) } } }
        includeMap[NAME_MATCH]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.nameMatches(it[n]) else includeMatcher!!.or(ElementMatchers.nameMatches(it[n])) } } }
        includeMap[IS_ANNOTATED_WITH]?.run { split(",").let { it.indices.forEach { n -> includeMatcher = if (null == includeMatcher) ElementMatchers.isAnnotatedWith(ElementMatchers.named(it[n])) else includeMatcher!!.or(ElementMatchers.isAnnotatedWith(ElementMatchers.named(it[n]))) } } }
        includeMatcher?.run { matcher.and(this) }
    }
    if (!excludeMap.isNullOrEmpty()) {
        excludeMap[NAMED]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.named(it[n]))) } } }
        excludeMap[NAME_START]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameStartsWith(it[n]))) } } }
        excludeMap[NAME_END]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameEndsWith(it[n]))) } } }
        excludeMap[NAME_CONTAINS]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameContains(it[n]))) } } }
        excludeMap[NAME_MATCH]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.nameMatches(it[n]))) } } }
        excludeMap[IS_ANNOTATED_WITH]?.run { split(",").let { it.indices.forEach { n -> matcher.and(ElementMatchers.not(ElementMatchers.isAnnotatedWith(ElementMatchers.named(it[n])))) } } }
    }
    
    return matcher
}