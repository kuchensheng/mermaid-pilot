# 介绍
mermaid-pilog-agent是基于javaagent技术开发的调用链信息收集客户端，进行字节码植入，从而对java应用程序进行相关数据采集和性能监控， 对应用无侵入。
# 源码
[https://github.com/kuchensheng/mermaid-pilot.git](https://github.com/kuchensheng/mermaid-pilot.git)

# 联系
```text
email: kucs@isyscore.com
微信： kuchenlove91
qq: 787211647
```

# agent 使用
## 1. 打包
```text
mvn mvn  clean assembly:assembly
```
打包生成的文件在/target目录下，包含依赖文件的jar：mermaid-pilot-agent-1.0-jar-with-dependencies.jar

## 2. vm配置
```text
-javaagent: \Ideaworkspace\mermaid-pilot\mermaid-pilot-agent\target\mermaid-pilot-agent-1.0-jar-with-dependencies.jar
```

## 3. 介绍

### 采集端
采集数据发送到APM Server端或者消息中间件或存储器等

### 采集功能
+ 分布式调用链（方法调用链路、远程调用追踪）
+ 采样控制
+ 方法采集
    - 方法调用链路
    - 参数采集
    - 执行耗时
    - 方法信息
    - 耗时阀值控制（超过一定时间阀值的不采集）
    - 采集点配置
    - 返回值采集 （暂未实现）
+ http追踪
    - 支持httpclient3
    - 支持httpclient4
    - 支持okhttp3
    - jdk-http（实现中）
+ 支持servlet采集
    - 主流容器支持（tomcat，weblogic，jetty等）
    - 链路支持
    -   基本请求信息
    -   耗时采集
    - 耗时阀值控制
    - request body采集
    - request parameter
    - request header采集
    - response body采集

+ jdbc采集（jdbc规范）
    - 采集sql语句
    - 采集sql参数
    - 采集执行时间
    - 耗时阀值控制
    - 采集结果集大小


+ gc、堆栈、内存、线程等采集
