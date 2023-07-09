import kotlinx.browser.document
import model.*
import react.create
import react.dom.client.createRoot

fun main() {
    val pageTypeBoard = document.getElementById("pageTypeBoard")
    if (pageTypeBoard != null) {
        boardPage()
    } else {
        lobbyPage()
    }
}

var boardID: String = ""
var userID: String = ""
val boardSubscription = BoardSubscription()

fun boardPage() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)
    boardID = document.URL.split("/")[4]
    console.log("boardID = $boardID")
    userID = document.getElementById("user")!!.innerHTML

    boardSubscription.subscribe()

    val root = createRoot(container)
    fetchBoardModel().then {
        val board = Board.create {
            boardModel = it
        }
        root.render(board)
    }

}

val lobbySubscription = LobbySubscription()


fun lobbyPage() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)
    lobbySubscription.subscribe()

    val root = createRoot(container)
    fetchLobbyModel().then {
        val lobby = Lobby.create {
            lobbyModel = it
        }
        root.render(lobby)
    }

}