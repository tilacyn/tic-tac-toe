package com.tilacyn.ttt.routes


import com.tilacyn.ttt.plugins.UserSession
import com.tilacyn.ttt.store.boardStore
import dto.Move
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay

//val charPool : CharRange = ('a'..'z')


fun Route.moveRouting() {
    authenticate("auth-session") {
        route("/v1/move") {
            put("{id?}") {
                println("move request accepted")
                val id = call.parameters["id"] ?: return@put call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                val board =
                    boardStore[id] ?: return@put call.respondText(
                        "No board with id $id",
                        status = HttpStatusCode.NotFound
                    )
                val move = call.receive<Move>()
                val userSession = call.principal<UserSession>()
                userSession ?: return@put call.respondText(
                    "Missing user session",
                    status = HttpStatusCode.Unauthorized
                )
                val validationResult = board.move(move, userSession.name)
                if (!validationResult.result) {
                    return@put call.respondText(
                        validationResult.message,
                        status = HttpStatusCode.Forbidden
                    )
                }
                return@put call.respondText(
                    "success",
                    status = HttpStatusCode.OK,
                )
            }
        }
    }
}