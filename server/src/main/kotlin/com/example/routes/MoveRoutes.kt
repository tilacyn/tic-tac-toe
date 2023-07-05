package com.example.routes



import com.example.models.*
import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

//val charPool : CharRange = ('a'..'z')


fun Route.moveRouting() {
    route("/move") {
        put("{id?}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val board =
                boardStorage[id] ?: return@put call.respondText(
                    "No board with id $id",
                    status = HttpStatusCode.NotFound
                )
            val move = call.receive<Move>()
            val validationResult = board.move(move)
        }
//        delete("{id?}") {
//            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//            if (customerStorage.removeIf { it.id == id }) {
//                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
//            } else {
//                call.respondText("Not Found", status = HttpStatusCode.NotFound)
//            }
//        }
    }
}