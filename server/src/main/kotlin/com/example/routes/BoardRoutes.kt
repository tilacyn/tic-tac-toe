package com.example.routes


import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

//val charPool : CharRange = ('a'..'z')

fun randomStringByKotlinRandom() = (1..10)
    .map { Random.nextInt(0, 26).let { 'a' + it } }
    .joinToString("")

fun createEmptyBoard(): Board {
    val id = randomStringByKotlinRandom()
    return Board(
        id,
        arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
        ),
        userMap = mapOf(
            Pair("hermione", 1),
            Pair("neville", 2)
        ),
        lastMove = ""

    )
}


fun Route.boardRouting() {
    route("/board") {
        get {
            if (boardStorage.isNotEmpty()) {
                call.respond(boardStorage)
            } else {
                call.respondText("No boards found", status = HttpStatusCode.OK)
            }
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                boardStorage[id] ?: return@get call.respondText(
                    "No board with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }
        post {
            val board = createEmptyBoard()
            boardStorage[board.id] = board
            call.respondText("Board stored correctly", status = HttpStatusCode.Created)
        }
    }
    route("/boardsN") {
        get {
            call.respondText(
                "3",
                status = HttpStatusCode.OK,
                /*configure = {
                    this.headers =
                }*/
//                        headersOf("Access-Control-Allow-Origin", "*")
            )
        }
    }
}