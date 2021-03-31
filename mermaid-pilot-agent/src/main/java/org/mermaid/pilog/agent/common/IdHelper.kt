package org.mermaid.pilog.agent.common

import java.lang.StringBuilder
import java.lang.management.ManagementFactory
import java.net.NetworkInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.getOrSet

/**
 * description: TraceId 设计实现,
 * 循环自增seq + 产生ID时间 + 机器MAC + 当前进程号 + 当前线程号
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/1911:24
 * @version 1.0
 */
private val seq = ThreadLocal<AtomicLong>().apply { set(AtomicLong(0)) }
private var spanIdMaxValue = ThreadLocal<ConcurrentHashMap<String,AtomicInteger>>()

private val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

/**
 * 获取mac地址
 * @return mac
 */
private fun getMac() :String = NetworkInterface.getNetworkInterfaces().run { StringBuilder().apply { while (hasMoreElements()) { nextElement().hardwareAddress?.let { it?.forEach {b-> append(hexByte(b)) } } } }.toString() }

/**
 * 获取进程Id
 * @return pid
 */
private fun getProcessId() = (ManagementFactory.getRuntimeMXBean().name.split("@")[0]).toInt()

/**
 * 生成traceId
 * 生成规则：循环自增seq + 产生ID时间 + 机器MAC + 当前进程号 + 当前线程号
 */
fun generateTraceId() = "%04d".format(seq.getOrSet { AtomicLong(0) }.getAndIncrement())+"${formatter.format(LocalDateTime.now())}${getProcessId()}"+"%05d".format(Thread.currentThread().id)

/**
 * 生成spanId
 * @param parentId 上一级spanId
 * @return 当前Span的Id
 */
fun generateSpanId(parentId: String) : String = spanIdMaxValue.getOrSet { ConcurrentHashMap<String,AtomicInteger>().apply { put(parentId,AtomicInteger(0)) } }?.let { it[parentId]?.getAndIncrement().toString() }

private fun hexByte(b:Byte) = "0000${Integer.toHexString(b.toInt())}".run { substring(length -2) }