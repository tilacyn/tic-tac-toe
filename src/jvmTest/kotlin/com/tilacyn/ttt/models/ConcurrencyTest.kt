package com.tilacyn.ttt.models

import com.tilacyn.ttt.store.BoardStore
import dto.Move
import io.ktor.util.collections.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.test.assertEquals

class ConcurrencyTest {

    private fun randomString() = (1..10)
        .map { Random.nextInt(0, 26).let { 'a' + it } }
        .joinToString("")


    @Test
    fun moves() {
        val store = BoardStore()
        var b: Board
        runBlocking {
            b = store.addEmptyBoard("hermione")
        }
        for (i in 0..9) {
            for (j in 0..9) {
                val usersConcurrent = List(7) {
                    randomString()
                }
                val validationResultsOKCount = AtomicInteger(0)
                runBlocking {
                    withContext(coroutineContext) {
                        for (it in usersConcurrent.indices) {
                            val user = usersConcurrent[it]
                            launch(Dispatchers.Default) {
                                val validationResult = b.move(Move(i, j), user)
                                if (validationResult.result) {
                                    validationResultsOKCount.incrementAndGet()
                                }
                            }
                        }
                    }
                }
                println(validationResultsOKCount.get())
                assertEquals(1, validationResultsOKCount.get())
            }
        }
    }

    @Test
    fun newUsers() {
        val store = BoardStore()
        var b: Board
        runBlocking {
            b = store.addEmptyBoard("hermione")
        }
        val m = ConcurrentSet<String>()
        runBlocking {
            withContext(coroutineContext) {
                for (it in 1..500) {
                    launch(Dispatchers.Default) {
                        val user = randomString()
                        b.assignNewUser(user)
                        m.add(user)
                    }
                }
            }
        }

        println(b.user2Symbol.keys.size)
        assertEquals(502, b.getNumericSymbolToAssign())
        assertEquals(501, b.user2Symbol.keys.size)
        assert(b.user2Symbol.keys.containsAll(m))
    }


}