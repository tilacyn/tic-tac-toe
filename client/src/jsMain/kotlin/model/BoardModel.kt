package model

import boardID
import dto.BoardDTO
import dto.Move
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise
import kotlinx.serialization.json.Json
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import react.StateSetter
import userID

private fun reqToBoardModel(req: XMLHttpRequest): BoardModel {
    val text = req.responseText
    val rawDTO = Json.decodeFromString<BoardDTO>(text)
    return BoardModel(rawDTO)
}


class BoardSubscription(var setter: StateSetter<BoardModel>? = null) {
    fun subscribe() {
        console.log("subscribing to board ws")
        val socket = WebSocket("ws://localhost:8080/ws/board/${boardID}")
        socket.addEventListener("message", {
            console.log("message received")
            console.log(it)
            val me = it.unsafeCast<MessageEvent>()
            console.log(me.data)
            val boardDTO = Json.decodeFromString<BoardDTO>(me.data.toString())
            console.log(boardDTO)
            if (setter != null) {
                setter!! {
                    BoardModel(boardDTO)
                }
            }
        })
    }

    fun setStateSetter(setter: StateSetter<BoardModel>) {
        this.setter = setter
    }
}


fun fetchBoardModel(): Promise<BoardModel> {
    return Promise { resolve, reject ->
        val req = basicApiRequest("GET", "board/${boardID}")
        req.addEventListener("load", {
            resolve(reqToBoardModel(req))
        })
        req.addEventListener("error", {
            reject(RuntimeException("failed to load board model: ${req.status}, ${req.responseText}"))
        })
        req.send()
    }
}

class BoardModel(var dto: BoardDTO) {

    fun getSymbol(): String {
        val ret = dto.user2Symbol[userID]
        return ret?: throw RuntimeException("failed to get user's symbol")
    }


    fun move(i: Int, j: Int): Promise<BoardModel> {
        return Promise { resolve, reject ->
            val req = basicApiRequest("PUT", "move/${boardID}")
            req.setRequestHeader("Content-type", "application/json; charset=utf-8")
            req.addEventListener("load", {
                resolve(reqToBoardModel(req))
            })
            req.addEventListener("error", {
                reject(RuntimeException("failed to load board model: ${req.status}, ${req.responseText}"))
            })
            req.send(JSON.stringify(Move(i, j)))
        }

    }
    fun copy(): BoardModel {
        return BoardModel(
            dto.copy(),
        )
    }
}


