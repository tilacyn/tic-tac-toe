package com.tilacyn.ttt.models

import com.tilacyn.ttt.store.BoardStore
import dto.BoardStatus
import dto.Move
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class BoardTest {

    @Test
    fun moves() {
        val store = BoardStore()
        val b = store.addEmptyBoard("hermione")

        assert(b.move(Move(1, 1), "hermione").result)
        assertFalse(b.move(Move(1, 1), "harry").result)
        assertFalse(b.move(Move(2, 2), "hermione").result)
        assert(b.move(Move(2, 2), "harry").result)
        assertFalse(b.move(Move(2, 2), "hermione").result)
        assert(b.move(Move(3, 3), "hermione").result)

        assertFalse(b.move(Move(10, 2), "ron").result)
        assertFalse(b.move(Move(2, 10), "ron").result)
        assertFalse(b.move(Move(-1, 3), "ron").result)
    }

    @Test
    fun fiveInARowHorizontal() {
        testTwoPlayerMovesOk(
            List(5) {
                Move(0, it)
            },
            List(4) {
                Move(it + 1, 0)
            },
            BoardStatus.FINISHED
        )
    }


    @Test
    fun fiveInARowVertical() {
        testTwoPlayerMovesOk(
            List(5) {
                Move(2+ it, 4)
            },
            List(4) {
                Move(it + 1, 0)
            },
            BoardStatus.FINISHED
        )
    }


    private fun testTwoPlayerMovesOk(firstPlayerMoves: List<Move>, secondPlayerMoves: List<Move>, status: BoardStatus) {
        val store = BoardStore()
        val b = store.addEmptyBoard("hermione")
        for (i in firstPlayerMoves.indices) {
            val move1 = firstPlayerMoves[i]
            val validationResult = b.move(move1, "hermione")
            assert(validationResult.result)
            if (i < firstPlayerMoves.size - 1) {
                assertEquals(b.getStatus(), BoardStatus.RUNNING)
            }
            if (i >= secondPlayerMoves.size) {
                continue
            }
            val move2 = secondPlayerMoves[i]
            b.move(move2, "neville")
            assert(validationResult.result)
            if (i < firstPlayerMoves.size - 1) {
                assertEquals(b.getStatus(), BoardStatus.RUNNING)
            }
        }
        assertEquals(status, b.getStatus())
    }

    @Test
    fun fiveInARowDiagonal1() {
        testTwoPlayerMovesOk(
            List(5) {
                Move(it + 2, it)
            },
            List(4) {
                Move(it + 1, it)
            },
            BoardStatus.FINISHED
        )
    }

    @Test
    fun fiveInARowDiagonal2() {
        testTwoPlayerMovesOk(
            List(5) {
                Move(5 - it, it)
            },
            List(4) {
                Move(6 - it, it)
            },
            BoardStatus.FINISHED
        )
    }

}