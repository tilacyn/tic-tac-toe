package com.tilacyn.ttt.routes

import com.tilacyn.ttt.store.Connection
import com.tilacyn.ttt.store.boardStore
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.logging.Logger

fun Route.configureSockets() {
    webSocket("/ws/board/{id?}") {
        val id = call.parameters["id"] ?: return@webSocket call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val c = Connection(this)
        val board =
            boardStore[id] ?: return@webSocket call.respondText(
                "No board with id $id",
                status = HttpStatusCode.NotFound
            )
        board.addConnection(c)
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
            }
        } catch (e: Exception) {
            Logger.getLogger(this.javaClass.name).warning(e.localizedMessage)
        } finally {
            board.removeConnection(c)
        }
    }


    webSocket("/ws/board") {
        val thisConnection = Connection(this)
        boardStore.addConnection(thisConnection)
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
            }
        } catch (e: Exception) {
            Logger.getLogger(this.javaClass.name).warning(e.localizedMessage)
        } finally {
            boardStore.removeConnection(thisConnection)
        }
    }
}