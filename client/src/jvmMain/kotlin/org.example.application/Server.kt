package org.example.application

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import kotlinx.html.*

fun HTML.index() {
    head {
        title("Lobby Page")
    }
    body {
        div {
            +"Lobby Page"
        }
        div {
            id = "root"
        }
        script(src = "/static/tic-tac-toe-client.js") {}
    }
}

fun HTML.board() {
    head {
        title("Board Page")
        link(rel = "stylesheet", href = "main.css") {}
    }
    body {
        div {
            id = "pageTypeBoard"
            +"Board Page"
        }
        div {
            id = "root"
        }
        script(src = "/static/tic-tac-toe-client.js") {}
//        style(src = "/static/main.css") { }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            get("/board/{id}") {
                call.respondHtml(HttpStatusCode.OK, HTML::board)
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}