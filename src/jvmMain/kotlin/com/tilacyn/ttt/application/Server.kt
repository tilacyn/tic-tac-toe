package com.tilacyn.ttt.application

import com.tilacyn.ttt.plugins.configureAuthentication
import com.tilacyn.ttt.plugins.configureSerialization
import com.tilacyn.ttt.plugins.configureWebsockets
import com.tilacyn.ttt.routes.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureAuthentication()
        configureWebsockets()
        routing {
            pageRoutes()
            boardRouting()
            moveRouting()
            authRouting()
            configureSockets()
        }
        configureSerialization()
    }.start(wait = true)
}