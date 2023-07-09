package com.example.routes


import com.example.models.*
import com.example.plugins.UserSession
import com.example.store.boardStorage
import com.example.store.boardStore
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random


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
                    boardStorage[id] ?: return@get call.respondText(
                        "No board with id $id",
                        status = HttpStatusCode.NotFound
                    )
                call.respond(board.toDTO())
            }
            post {
                val userSession = call.principal<UserSession>()
                userSession ?: return@post call.respondText(
                    "Missing user session",
                    status = HttpStatusCode.Unauthorized
                )
                val board = createEmptyBoard(userSession.name)
                boardStorage[board.id] = board
                boardStore.changed()
                call.respond(status = HttpStatusCode.Created, board.id)
            }
        }
    }
}