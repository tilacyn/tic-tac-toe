package com.tilacyn.ttt.routes


import com.tilacyn.ttt.plugins.UserSession
import com.tilacyn.ttt.store.boardStore
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.boardRouting() {
    authenticate("auth-session") {
        route("/v1/board") {
            get {
                val ret = boardStore.toDTO()
                call.respond(ret)
            }
            get("{id?}") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                val board =
                    boardStore[id] ?: return@get call.respondText(
                        "No board with id $id",
                        status = HttpStatusCode.NotFound
                    )
                call.respond(board.toDTO(true))
            }
            post {
                val userSession = call.principal<UserSession>()
                userSession ?: return@post call.respondText(
                    "Missing user session",
                    status = HttpStatusCode.Unauthorized
                )
                val board = boardStore.addEmptyBoard(userSession.name)
                call.respond(status = HttpStatusCode.Created, board.id)
            }
        }
    }
}