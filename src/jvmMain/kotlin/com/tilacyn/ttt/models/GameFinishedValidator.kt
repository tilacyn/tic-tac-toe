package com.tilacyn.ttt.models

import kotlin.math.max
import kotlin.math.min


data class GameResult(var winner: String = "", val finished: Boolean)

class GameFinishedValidator(private val array: List<MutableList<String>>, private val symbol2User: Map<String, String>) {
    fun run(): GameResult {
        array.forEach {
            val res = fiveInARow(it)
            if (res.finished) {
                return res
            }
        }
        for (i in 0..9) {
            val res = fiveInARow(array.map { it[i] })
            if (res.finished) {
                return res
            }
        }

        for (i in 4..14) {
            val list = mutableListOf<String>()
            for (k in max(0, i - 9)..min(i, 9)) {
                list.add(array[k][i - k])
                val res = fiveInARow(list)
                if (res.finished) {
                    return res
                }
            }
        }

        for (i in 4..14) {
            val list = mutableListOf<String>()
            for (k in max(0, i - 9)..min(i, 9)) {
                list.add(array[9 - k][i - k])
                val res = fiveInARow(list)
                if (res.finished) {
                    return res
                }
            }
        }
        if (!array.flatten().contains("")) {
            return GameResult("", true)
        }
        return GameResult("", false)
    }

    private fun fiveInARow(list: List<String>): GameResult {
        var prev = ""
        var prevLength = 0
        list.forEach {
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
                return GameResult(symbol2User[prev]?: "", true)
            }
        }
        return GameResult("", false)
    }
}
