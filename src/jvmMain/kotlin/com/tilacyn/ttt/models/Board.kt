package com.tilacyn.ttt.models

import com.tilacyn.ttt.store.Connection
import com.tilacyn.ttt.store.boardStore
import dto.BoardDTO
import dto.BoardStatus
import dto.Move
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger


class Board(
    val id: String,
    private val array: List<MutableList<String>>,
    val user2Symbol: MutableMap<String, String>,
    private var lastMove: String,
    var lastMoveTimestamp: Instant?,
    private val connections: MutableSet<Connection> = ConcurrentSet(),
    private var numericSymbolToAssign: AtomicInteger = AtomicInteger(1),
    private var status: BoardStatus = BoardStatus.RUNNING,
    private var winner: String = ""
) {
    fun toDTO(arrayRequired: Boolean): BoardDTO {
        val arrayPayload = when (arrayRequired) {
            true -> array
            else -> null
        }
        return BoardDTO(id, arrayPayload, lastMove, user2Symbol, lastMoveTimestamp?.toString(), status, winner)
    }

    fun getStatus(): BoardStatus {
        return status
    }

    @Synchronized
    private fun moveSynchronized(m: Move, user: String): ValidationResult {
        val validationResult = validateMove(m, user)
        if (!validationResult.result) {
            return validationResult
        }

        array[m.x][m.y] = user2Symbol[user] ?: doAssignNewUser(user)
        lastMove = user
        lastMoveTimestamp = Instant.now()

        val gameResult = validateGameFinished()
        if (gameResult.finished) {
            status = BoardStatus.FINISHED
            winner = gameResult.winner
        }
        return validationResult
    }

    suspend fun move(m: Move, user: String): ValidationResult {
        val validationResult = moveSynchronized(m, user)
        if (validationResult.result) {
            changed()
        }
        return validationResult
    }

    private suspend fun changed() = coroutineScope {
        val message = Json.encodeToString(serializer(), toDTO(true))
        for (c: Connection in connections) {
            launch(Dispatchers.IO) {
                c.session.send(message)
            }
        }
        launch(Dispatchers.Default) {
            boardStore.changed()
        }
    }

    private fun doAssignNewUser(user: String): String {
        val symbol = numericSymbolToAssign.getAndIncrement().toString()
        user2Symbol[user] = symbol
        return symbol
    }

    suspend fun assignNewUser(user: String): String {
        val symbol = doAssignNewUser(user)
        changed()
        return symbol
    }


    fun addConnection(c: Connection) {
        connections.add(c)
    }


    private fun validateGameFinished(): GameResult {
        return GameFinishedValidator(array, user2Symbol.entries.associateBy({ it.value }) { it.key }).run()
    }

    private fun validateMove(m: Move, user: String): ValidationResult {
        if (status == BoardStatus.FINISHED) {
            return ValidationResult("game is finished", false)
        }
        if (lastMove == user) {
            return ValidationResult("user cannot move twice in a row", false)
        }
        if (m.x >= array.size || m.y >= array[0].size || m.x < 0 || m.y < 0) {
            return ValidationResult("illegal coordinates", false)
        }

        if (array[m.x][m.y] != "") {
            return ValidationResult("cell occupied", false)
        }
        return ValidationResult("", true)
    }

    fun removeConnection(thisConnection: Connection) {
        connections.remove(thisConnection)
    }

    fun getNumericSymbolToAssign(): Int {
        return numericSymbolToAssign.get()
    }

    fun getWinner(): String {
        return winner
    }
}


data class ValidationResult(val message: String, val result: Boolean)
