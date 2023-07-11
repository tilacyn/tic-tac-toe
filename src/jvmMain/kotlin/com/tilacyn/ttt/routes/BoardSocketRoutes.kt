package com.tilacyn.ttt.routes

import com.tilacyn.ttt.store.Connection
import com.tilacyn.ttt.store.boardStore
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.configureSockets() {
//        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
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
                val receivedText = frame.readText()
                println("Received: $receivedText")
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $c!")
            board.removeConnection(c)
        }

    }


    webSocket("/ws/board") {
        println("Connected to lobby!")

        val thisConnection = Connection(this)
        boardStore.addLobbyConnection(thisConnection)
        println("added connection!")
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                println("Received: $receivedText")
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $thisConnection!")
            boardStore.removeConnection(thisConnection)
        }

    }
}