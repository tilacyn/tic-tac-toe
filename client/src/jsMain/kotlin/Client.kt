import kotlinx.browser.document
import kotlinx.browser.window
import model.emptyBoardModel
import model.emptyLobbyModel
import model.fetchLobbyModel
import org.w3c.dom.Option
import org.w3c.dom.get
import org.w3c.xhr.XMLHttpRequest
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

fun boardPage() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val board = Board.create {
        boardModel = emptyBoardModel()
    }

    val root = createRoot(container)
    root.render(board)

}

fun lobbyPage() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

//    val welcome = Welcome.create {
//        name = "Kotlin/JS"
//    }
    fetchLobbyModel().then({ lobbyModel1 ->
        {
            console.log("received lobby model, rendering")
//            val lobby1 = Lobby.create {
//                lobbyModel = lobbyModel1
//            }
//            root.render(lobby1)
        }
    }, { rejected ->
        {
            console.log("rejected: ${rejected.message}")
        }
    })
    val lobby = Lobby.create {
        lobbyModel = emptyLobbyModel()
    }
    val root = createRoot(container)
    root.render(lobby)

//    fetchLobbyModel().then({ m1 -> {} }, { m2 -> {} })

}

fun addHandlers() {
    console.log("adding handlers")
    val element = document.getElementById("board1")
    console.log(element)
    if (element != null) {
        console.log("element != null")
        element.addEventListener("click", {
            console.log("handling click")
            window.open("/board1");
        })
    } else {
        console.log("element == null")
    }
}