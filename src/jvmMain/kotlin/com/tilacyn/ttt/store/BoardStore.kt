package com.tilacyn.ttt.store

import com.tilacyn.ttt.models.Board
import dto.BoardDTO
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json.Default.encodeToString
import kotlinx.serialization.serializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class BoardStore(
    private val lobbyConnections: MutableSet<Connection> = ConcurrentSet(),
    private val boardStorage: MutableMap<String, Board> = ConcurrentHashMap()
) {

    suspend fun changed() = coroutineScope {
        for (c: Connection in lobbyConnections) {
            launch(Dispatchers.Default) {
                c.session.send(encodeToString(serializer(), toDTO()))
            }
        }
    }

    fun toDTO(): List<BoardDTO> {
        val ret = boardStorage.values.sortedByDescending {
            it.lastMoveTimestamp
        }.map {
            it.toDTO(false)
        }
        return ret
    }

    fun addLobbyConnection(c: Connection) {
        lobbyConnections.add(c)
    }

    fun removeConnection(c: Connection) {
        lobbyConnections.remove(c)
    }

    operator fun get(id: String): Board? {
        return boardStorage[id]
    }

    operator fun set(id: String, board: Board) {
        boardStorage[id] = board
    }

    suspend fun addEmptyBoard(user: String): Board {
        val id = randomString10()
        val ret = Board(
            id,
            List(10) { MutableList(10) { "" } },
            user2Symbol = ConcurrentHashMap(),
            lastMove = "",
            lastMoveTimestamp = null
        )
        ret.assignNewUser(user)
        this[id] = ret
        changed()
        return ret
    }

}

fun randomString10() = (1..10)
    .map { Random.nextInt(0, 26).let { 'a' + it } }
    .joinToString("")


val boardStore = BoardStore()


class Connection(val session: DefaultWebSocketSession)


