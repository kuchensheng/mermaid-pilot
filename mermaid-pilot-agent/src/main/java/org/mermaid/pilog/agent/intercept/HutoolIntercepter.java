package org.mermaid.pilog.agent.intercept;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 *
 * @author 库陈胜
 * @version 1.0
 * @date 2021/3/413:39
 */
public class HutoolIntercepter {

    @RuntimeType
    public static Object intercept(@Origin(privileged = true) Method method, @This(optional = true) Object instance, @SuperCall Callable<?> callable) throws Exception {
        System.out.println("执行方法了……methodName is "+ method.getName());
        return callable.call();
    }
}
