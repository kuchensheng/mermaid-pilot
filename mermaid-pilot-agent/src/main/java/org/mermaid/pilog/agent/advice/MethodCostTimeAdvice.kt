package org.mermaid.pilog.agent.advice

import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.SuperCall
import org.mermaid.pilog.agent.common.produce
import org.mermaid.pilog.agent.model.getCurrentSpan
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/2220:55
 * @version 1.0
 */
class MethodCostTimeAdvice {
    companion object {
        @RuntimeType
        @JvmStatic
        fun intercept(@Origin method: Method, @SuperCall callable: Callable<*>): Any? {
            try {
                return callable.call()
            } finally {
                getCurrentSpan()?.run {
                    endTime = LocalDateTime.now()
                    costTime = Duration.between(startTime,endTime).toMillis()
                    produce(this)
                }
            }

        }
    }
}