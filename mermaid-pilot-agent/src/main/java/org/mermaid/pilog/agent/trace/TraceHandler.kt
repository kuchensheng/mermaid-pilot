package org.mermaid.pilog.agent.trace

import net.bytebuddy.asm.Advice
/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 78721
 * @date 2021/2/1011:45
 * @version 1.0
 */
class TraceHandler {

    @Advice.OnMethodEnter
    fun enter(@Advice.Origin("#t") calssName:String, @Advice.Origin("#m") methodName:String) {

    }
}