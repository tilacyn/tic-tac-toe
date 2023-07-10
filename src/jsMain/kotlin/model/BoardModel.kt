package model

import dto.BoardDTO
import dto.Move
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise
import kotlinx.serialization.json.Json
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import react.StateSetter

private fun reqToBoardModel(req: XMLHttpRequest): BoardModel {
    val text = req.responseText
    val rawDTO = Json.decodeFromString<BoardDTO>(text)
    return BoardModel(rawDTO)
}

class BoardData(
    var boardID: String = "",
    var userID: String = "",
)

val boardData = BoardData()


class BoardSubscription(private var setter: StateSetter<BoardModel>? = null) {
    fun subscribe() {
        console.log("subscribing to board ws")
        val socket = WebSocket("ws://localhost:8080/ws/board/${boardData.boardID}")
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
        val req = basicApiRequest("GET", "board/${boardData.boardID}")
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


    fun move(i: Int, j: Int) {
        val req = basicApiRequest("PUT", "move/${boardData.boardID}")
        req.setRequestHeader("Content-type", "application/json; charset=utf-8")
        req.send(JSON.stringify(Move(i, j)))
    }


}


