package org.example.application

import com.example.plugins.configureAuthentication
import com.example.plugins.configureSerialization
import com.example.plugins.configureWebsockets
import com.example.routes.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import kotlinx.html.*



fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureAuthentication()
        configureWebsockets()
        routing {
            pageRoutes()
            customerRouting()
            boardRouting()
            moveRouting()
            authRouting()
            configureSockets()
        }
        configureSerialization()
    }.start(wait = true)
}