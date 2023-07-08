package model

import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise


fun reqToLobbyModel(req: XMLHttpRequest): LobbyModel {
    return LobbyModel(
        listOf(),
        req.responseText
    )
}

fun emptyLobbyModel(): LobbyModel {
    return LobbyModel(
        listOf(),
        "empty"
    )
}

fun emptyBoardModel(): BoardModel {
    return BoardModel(
        listOf(
            intArrayOf(0, 0, 1, 1),
            intArrayOf(2, 0, 1, 1),
            intArrayOf(1, 0, 1, 1),
            intArrayOf(0, 0, 2, 2)
        ),
        mapOf()
    )
}

fun fetchLobbyModel(): Promise<LobbyModel> {
    return Promise { resolve, reject ->
        val req = basicApiRequest("GET", "boardsN")
        req.addEventListener("load", {
            console.log("load success")
            resolve(reqToLobbyModel(req))
        })
        req.addEventListener("error", {
            reject(RuntimeException("failed to load lobby model: ${req.status}, ${req.responseText}"))
        })
        req.open("GET", "http://localhost:8081/boardsN")
        req.setRequestHeader("Access-Control-Allow-Origin", "*")
        req.send()
    }

}

class BoardModel(var array: List<IntArray>, var playersMap: Map<String, Int>) {

}

class LobbyModel(var boards: List<BoardModel>, var n: String) {
//    var boards: Array<BoardModel>
}