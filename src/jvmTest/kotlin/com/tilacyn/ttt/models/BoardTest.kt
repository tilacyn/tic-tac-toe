package com.tilacyn.ttt.models

import com.tilacyn.ttt.store.BoardStore
import com.tilacyn.ttt.store.randomString10
import dto.BoardStatus
import dto.Move
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BoardTest {

    @Test
    fun moves() {
        val store = BoardStore()
        runBlocking {
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
    }

    @Test
    fun gameIsDraw() {
        val store = BoardStore()
        runBlocking {
            val b = store.addEmptyBoard("hermione")
            for (i in 0..9) {
                for (j in 0..9) {
                    assertTrue(b.move(Move(i, j), randomString10()).result)
                    if (i != 9 || j != 9) {
                        assertEquals(BoardStatus.RUNNING, b.getStatus())
                    } else {
                        assertEquals(BoardStatus.FINISHED, b.getStatus())
                        assertEquals("", b.getWinner())
                    }
                }
            }
        }
    }

    @Test
    fun fiveInARowHorizontal() {
        twoPlayerMovesFirstWins(
            List(5) {
                Move(0, it)
            },
            List(4) {
                Move(it + 1, 0)
            }
        )
    }


    @Test
    fun fiveInARowVertical() {
        twoPlayerMovesFirstWins(
            List(5) {
                Move(2+ it, 4)
            },
            List(4) {
                Move(it + 1, 0)
            },
        )
    }


    private fun twoPlayerMovesFirstWins(firstPlayerMoves: List<Move>, secondPlayerMoves: List<Move>) = runBlocking {
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
            runBlocking {
                b.move(move2, "neville")
            }
            assert(validationResult.result)
            if (i < firstPlayerMoves.size - 1) {
                assertEquals(b.getStatus(), BoardStatus.RUNNING)
            }
        }
        assertEquals(BoardStatus.FINISHED, b.getStatus())
        assertEquals("hermione", b.getWinner())
    }

    @Test
    fun fiveInARowDiagonal1() {
        twoPlayerMovesFirstWins(
            List(5) {
                Move(it + 2, it)
            },
            List(4) {
                Move(it + 1, it)
            },
        )
    }

    @Test
    fun fiveInARowDiagonal2() {
        twoPlayerMovesFirstWins(
            List(5) {
                Move(5 - it, it)
            },
            List(4) {
                Move(6 - it, it)
            },
        )
    }

}