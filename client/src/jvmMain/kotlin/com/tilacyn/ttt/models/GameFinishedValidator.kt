package com.tilacyn.ttt.models

import kotlin.math.max
import kotlin.math.min


class GameFinishedValidator(private val array: List<MutableList<String>>) {
    fun run(): Boolean {
        array.forEach {
            if (fiveInARow(it)) {
                return true
            }
        }
        for (i in 0..9) {
            if (fiveInARow(array.map { it[i] })) {
                return true
            }
        }

        for (i in 4..14) {
            val list = mutableListOf<String>()
            for (k in max(0, i - 9)..min(i, 9)) {
                list.add(array[k][i - k])
                if (fiveInARow(list)) {
                    return true
                }
            }
        }

        for (i in 4..14) {
            val list = mutableListOf<String>()
            for (k in max(0, i - 9)..min(i, 9)) {
                list.add(array[9 - k][i - k])
                if (fiveInARow(list)) {
                    return true
                }
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
                return true
            }
        }
        return false
    }
}
