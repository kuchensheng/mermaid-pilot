package org.mermaid.pilot.platform

import io.ktor.application.*
import io.ktor.http.websocket.*
import io.ktor.routing.*
import io.ktor.server.netty.*

/**
 * description: TODO
 * copyright: Copyright (c) 2018-2021
 * company: iSysCore Tech. Co., Ltd.
 * @author 库陈胜
 * @date 2021/2/249:37
 * @version 1.0
 */

fun main(args: Array<String>) : Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    routing {
        post() {

        }

    }
}