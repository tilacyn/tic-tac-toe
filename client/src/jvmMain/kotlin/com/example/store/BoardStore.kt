package com.example.store

import com.example.models.Board
import dto.BoardDTO
import io.ktor.serialization.kotlinx.*
import io.ktor.util.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

class BoardStore(private val lobbyConnections: MutableSet<Connection> = mutableSetOf()) {

    // todo concurrency
    @OptIn(InternalAPI::class)
    suspend fun changed() {
        println("board changed")
        for (c: Connection in lobbyConnections) {
            c.session.sendSerializedBase<List<BoardDTO>>(
                toDTO(),
                KotlinxWebsocketSerializationConverter(Json),
//                converter = KotlinxWebsocketSerializationConverter(Json),
                Charset.defaultCharset(),
            )
            println("message sent")
        }
    }

    fun toDTO(): List<BoardDTO> {
        val ret = boardStorage.values.sortedByDescending {
            it.lastMoveTimestamp
        }.map {
            it.toDTO()
        }
        return ret
    }

    fun addLobbyConnection(c: Connection) {
        lobbyConnections.add(c)
    }

    fun removeConnection(c: Connection) {
        lobbyConnections.remove(c)
    }
}

val boardStore = BoardStore()
val boardStorage: MutableMap<String, Board> = mutableMapOf()


class Connection(val session: DefaultWebSocketSession)


