package model

import dto.BoardDTO
import kotlinx.serialization.json.Json
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.xhr.XMLHttpRequest
import react.StateSetter
import kotlin.js.Promise


fun reqToLobbyModel(req: XMLHttpRequest): LobbyModel {
    val text = req.responseText
    val rawDTO = Json.decodeFromString<List<BoardDTO>>(text)
    return LobbyModel(
        rawDTO,
    )
}

fun fetchLobbyModel(): Promise<LobbyModel> {
    return Promise { resolve, reject ->
        val req = basicApiRequest("GET", "board")
        req.addEventListener("load", {
            resolve(reqToLobbyModel(req))
        })
        req.addEventListener("error", {
            reject(RuntimeException("failed to load boards: ${req.status}, ${req.responseText}"))
        })
        req.send()
    }
}

class LobbySubscription(private var setter: StateSetter<LobbyModel>? = null) {
    fun subscribe() {
        console.log("subscribing to board ws")
        val socket = WebSocket("ws://localhost:8080/ws/board")
        socket.addEventListener("message", {
            console.log("message received")
            console.log(it)
            val me = it.unsafeCast<MessageEvent>()
            console.log(me.data)
            val boardDTOs = Json.decodeFromString<List<BoardDTO>>(me.data.toString())
            console.log(boardDTOs)
            if (setter != null) {
                console.log("setter invoked")
                setter!! {
                    LobbyModel(boardDTOs)
                }
            }
        })
    }

    fun setStateSetter(setter: StateSetter<LobbyModel>) {
        this.setter = setter
    }
}


class LobbyModel(var boardDescriptions: List<BoardDTO>) {

    //    var boards: Array<BoardModel>
    fun createBoard(): Promise<String> {
        return Promise { resolve, reject ->
            val req = basicApiRequest("POST", "board")
            req.addEventListener("load", {
                console.log("load success")
                console.log(req)
                val id = req.responseText
                resolve(id)
            })
            req.addEventListener("error", {
                reject(RuntimeException("failed to create board: ${req.status}, ${req.responseText}"))
            })
            req.send()
        }
    }
}




