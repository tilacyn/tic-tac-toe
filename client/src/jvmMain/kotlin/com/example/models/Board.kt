package com.example.models

import com.example.store.Connection
import com.example.store.boardStore
import dto.BoardDTO
import dto.BoardStatus
import dto.Move
import io.ktor.serialization.kotlinx.*
import io.ktor.util.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.time.Instant
import kotlin.random.Random


class Board(
    val id: String,
    private val array: List<MutableList<String>>,
    val user2Symbol: MutableMap<String, String>,
    var lastMove: String,
    var lastMoveTimestamp: Instant?,
    private val fullConnections: MutableSet<Connection> = mutableSetOf(),
    private var numericSymbolToAssign: Int = 1,
    private var status: BoardStatus = BoardStatus.RUNNING
) {
    fun toDTO(): BoardDTO {
        return BoardDTO(id, array, lastMove, user2Symbol, lastMoveTimestamp.toString(), status)
    }

    fun move(m: Move, user: String): ValidationResult {
        val validationResult = validateMove(m, user)
        if (!validationResult.result) {
            return validationResult
        }

        array[m.x][m.y] = user2Symbol[user] ?: assignNewUser(user)
        lastMove = user
        lastMoveTimestamp = Instant.now()



        if (validateGameFinished()) {
            status = BoardStatus.FINISHED
        }
        println("suspend changed")

        //        todo non blocking?
        blockingChanged()
        return validationResult
    }

    fun blockingChanged() {
        runBlocking {
            launch {
                changed()
                boardStore.changed()
            }
        }
    }

    fun assignNewUser(user: String): String {
        val symbol = numericSymbolToAssign++.toString()
        user2Symbol[user] = symbol
        blockingChanged()
        return symbol
    }

    // todo concurrency
    @OptIn(InternalAPI::class)
    suspend fun changed() {
        println("board changed")
        for (c: Connection in fullConnections) {
            c.session.sendSerializedBase<BoardDTO>(
                toDTO(),
                KotlinxWebsocketSerializationConverter(Json),
//                converter = KotlinxWebsocketSerializationConverter(Json),
                Charset.defaultCharset(),
            )
            println("message sent")
        }
    }


    fun addConnection(c: Connection) {
        fullConnections.add(c)
    }


    private fun validateGameFinished(): Boolean {
        return GameFinishedValidator(array).run()
    }

    private fun validateMove(m: Move, user: String): ValidationResult {
        if (status == BoardStatus.FINISHED) {
            return ValidationResult("game is finished", false)
        }
        if (lastMove == user) {
            return ValidationResult("user cannot move twice in a row", false)
        }
        if (m.x >= array.size || m.y >= array[0].size) {
            return ValidationResult("illegal coordinates", false)
        }

        if (array[m.x][m.y] != "") {
            return ValidationResult("cell occupied", false)
        }
        return ValidationResult("", true)
    }

    fun removeConnection(thisConnection: Connection) {
        fullConnections.remove(thisConnection)
    }
}


// todo make it into an exception
data class ValidationResult(val message: String, val result: Boolean)


fun randomStringByKotlinRandom() = (1..10)
    .map { Random.nextInt(0, 26).let { 'a' + it } }
    .joinToString("")


fun createEmptyBoard(user: String): Board {
    val id = randomStringByKotlinRandom()
    val ret = Board(
        id,
        MutableList(10) { MutableList(10) {""} },
        user2Symbol = mutableMapOf(),
        lastMove = "" ,
        lastMoveTimestamp = null
    )
    ret.assignNewUser(user)
    return ret
}


class GameFinishedValidator(val array: List<MutableList<String>>) {
    fun run(): Boolean {
        array.forEach{
            if (fiveInARow(it)) {
                return true
            }
        }
        for (i in 0..9) {
            if (fiveInARow(array.map { it[i] })) {
                return true
            }
        }
//        todo diagonal
//        for (i in 0..19) {
//            if (fiveInARow(array.map { it[i] })) {
//                return true
//            }
//        }
        return false
    }

    private fun fiveInARow(list: List<String>): Boolean {
        var prev = ""
        var prevLength = 0
        list.forEach{
            if (it == prev) {
                prevLength++
            } else {
                prevLength = 1
            }
            prev = it
            if (it == "") {
                prevLength = 0
            }
            if (prevLength >= 5) {
                return true
            }
        }
        return false
    }
}
