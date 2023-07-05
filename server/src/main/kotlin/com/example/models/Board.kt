package com.example.models

import kotlinx.serialization.Serializable


//@Serializable
//enum class Cell{X, O}

@Serializable
data class Move(val userID: String, val x: Int, val y: Int)

@Serializable
data class Board(val id: String, val arr: Array<IntArray>, val userMap: Map<String, Int>, var lastMove: String)



fun Board.move(m: Move) : ValidationResult {
    val validationResult = validateMove(m)
    if (!validationResult.result) {
        return validationResult
    }

    arr[m.x][m.y] = userMap[m.userID]!!.toInt()
    return validationResult
}

// todo make it into an exception
data class ValidationResult(val message: String, val result: Boolean)

fun Board.validateMove(m: Move) : ValidationResult {
    if (lastMove == m.userID) {
        return ValidationResult("user cannot move twice in a row", false)
    }
    if (m.x >= arr.size || m.y >= arr[0].size) {
        return ValidationResult("illegal coordinates", false)
    }

    if (arr[m.x][m.y] != 0) {
        return ValidationResult("cell occupied", false)
    }
    return ValidationResult("", true)
}

val boardStorage: HashMap<String, Board> = HashMap()
