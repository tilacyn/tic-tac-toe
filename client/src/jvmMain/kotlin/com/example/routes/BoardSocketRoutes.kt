package com.example.routes

import com.example.store.Connection
import com.example.store.boardStorage
import com.example.store.boardStore
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.configureSockets() {
//        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/ws/board/{id?}") {
            println("Connected to board!")
            val gameFieldRequired = call.parameters["gameFieldRequired"]
            val id = call.parameters["id"] ?: return@webSocket call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val thisConnection = Connection(this)
            val board =
                boardStorage[id] ?: return@webSocket call.respondText(
                    "No board with id $id",
                    status = HttpStatusCode.NotFound
                )
//            if (gameFieldRequired.toBoolean()) {
                board.addConnection(thisConnection)
//            }
            thisConnection.session.send("connected wow cool")
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
                if (gameFieldRequired.toBoolean()) {
                    board.removeConnection(thisConnection)
                } else {
                }
            }

    }


    webSocket("/ws/board") {
            println("Connected to lobby!")

            val thisConnection = Connection(this)
            boardStore.addLobbyConnection(thisConnection)
            thisConnection.session.send("connected wow cool")
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