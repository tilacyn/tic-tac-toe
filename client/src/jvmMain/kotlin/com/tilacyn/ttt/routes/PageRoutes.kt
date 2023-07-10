package com.tilacyn.ttt.routes

import com.tilacyn.ttt.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.dom.document


fun HTML.index() {
    head {
        title("Lobby Page")
    }
    body {

        div {
            id = "root"
        }

        script(src = "/static/tic-tac-toe-client.js") {}
    }
}

fun HTML.board(user: String) {
    head {
        title("Board Page")
        link(rel = "stylesheet", href = "main.css") {}
    }
    body {
        div {
            id = "pageTypeBoard"
            hidden = true
        }
        div {
            hidden = true
            id = "user"
            +user
        }
        div {
            id = "root"
        }
        script(src = "/static/tic-tac-toe-client.js") {}
    }
}

fun Route.pageRoutes() {
    authenticate("auth-session") {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
    }
    authenticate("auth-session") {
        get("/board/{id}") {
            val userSession = call.principal<UserSession>()
            userSession ?: return@get call.respondText(
                "Missing user session",
                status = HttpStatusCode.Unauthorized
            )
            call.respondHtml(HttpStatusCode.OK) {
                board(userSession.name)
            }
        }
    }
    static("/static") {
        resources()
    }

}