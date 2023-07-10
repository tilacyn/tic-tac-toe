import csstype.*
import dto.BoardDTO
import dto.BoardStatus
import emotion.react.css
import kotlinx.browser.window
import model.LobbyModel
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.tr
import react.useState
import kotlin.js.Date

external interface LobbyProps : Props {
    var lobbyModel: LobbyModel
}

val Lobby = FC<LobbyProps> { props ->
    val instance = useState(props.lobbyModel)
    val model = instance.component1()
    val setter = instance.component2()
    lobbySubscription.setStateSetter(setter)

    header(model)
    if (model.boardDescriptions.isEmpty()) {
        noGamesYet()
    } else {
        gamesTable(model)
    }
}



fun ChildrenBuilder.noGamesYet() {
    div {
        css {
            margin = Auto.auto
            marginTop = 100.px
            fontSize = 40.px
            fontFamily = FontFamily.fantasy
            textAlign = TextAlign.center
        }
        +"No games yet"
    }
}


fun ChildrenBuilder.header(model: LobbyModel) {
    div {
        css {
            padding = 35.px
            color = rgb(100, 100, 100)
            fontSize = 40.px
            fontFamily = FontFamily.fantasy
            fontWeight = FontWeight.bold
            margin = Auto.auto
            textAlign = TextAlign.center
        }
        +"Lobby"

    }


    div {
        css {
            display = Display.flex;
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center

            padding = 50.px
            margin = Auto.auto
            fontSize = 20.px
            textAlign = TextAlign.center
            fontFamily = FontFamily.fantasy
        }
        button {

            css {
                margin = Auto.auto
                color = rgb(240, 240, 240)
                textAlign = TextAlign.center
                width = 100.px
                height = 100.px
                backgroundColor = rgb(60, 60, 60)
                hover {
                    backgroundColor = rgb(150, 150, 150)
                }
                fontFamily = FontFamily.fantasy
                borderStyle = LineStyle.solid
                borderWidth = 4.px
                borderColor = rgb(40, 40, 40)
                borderRadius = 10.px
            }

            onClick = {
                model.createBoard().then {
                    window.open("/board/${it}")
                }
            }
            +"New Game"
        }
    }

    div {
        css {
            backgroundColor = rgb(70, 70, 70)
            marginTop = 50.px
            margin = Auto.auto
            width = 50.pct
            height = 2.px
        }
    }


}


fun ChildrenBuilder.gamesTableHeader() {
    tr {
        th {}
        th {
            css {
                backgroundColor = rgb(10, 10, 10)
                color = rgb(255, 255, 255)
                fontSize = 20.px
                fontFamily = FontFamily.fantasy
            }
            +"Status"
        }
        th {
            css {
                backgroundColor = rgb(10, 10, 10)
                color = rgb(255, 255, 255)
                fontSize = 20.px
                fontFamily = FontFamily.fantasy
            }
            +"Players"
        }
        th {
            css {
                backgroundColor = rgb(10, 10, 10)
                color = rgb(255, 255, 255)
                fontSize = 20.px
                fontFamily = FontFamily.fantasy
            }
            +"Last move at"
        }
    }
}
fun ChildrenBuilder.gamesTable(model: LobbyModel) {
    table {
        css {
            margin = Auto.auto
            marginTop = 100.px
        }
        gamesTableHeader()

        model.boardDescriptions.forEach { dto ->
            val players = dto.user2Symbol.keys.joinToString(", ")
            tr {
                css {
                    height = 80.px
                    borderRadius = 9.px
                    borderColor = NamedColor.white
                    borderWidth = 5.px
                    backgroundColor = rgb(150, 150, 150)

                }
                joinCell(dto)
                statusCell(dto)
                playersCell(players)
                lastMoveCell(dto)
            }
        }
    }
}

fun ChildrenBuilder.joinCell(dto: BoardDTO) {
    td {
        div {
            css {
                height = 40.px
            }
            button {
                css {
                    color = rgb(255, 255, 255)
                    fontSize = 20.px
                    fontFamily = FontFamily.fantasy
                    backgroundColor = rgb(60, 60, 60)
                    borderRadius = 10.px
                    hover {
                        backgroundColor = rgb(150, 150, 150)
                    }
                }
                +"Join"
                onClick = {
                    window.open("/board/${dto.id}")
                }
            }
        }
    }
}

fun ChildrenBuilder.statusCell(dto: BoardDTO) {
    td {
        div {
            css {
                fontSize = 15.px
                fontFamily = FontFamily.fantasy
                fontWeight = FontWeight.bold
                marginRight = 10.px
                color = when (dto.status) {
                    BoardStatus.FINISHED -> {
                        NamedColor.red
                    }
                    else -> {
                        NamedColor.darkgreen
                    }
                }
            }
            +dto.status.toString()
        }
    }
}

fun ChildrenBuilder.playersCell(players: String) {
    td {
        div {
            css {
                marginLeft = 150.px
                fontSize = 15.px
                fontFamily = FontFamily.fantasy
                marginRight = 20.px
            }
            +players
        }
    }
}

fun ChildrenBuilder.lastMoveCell(dto: BoardDTO) {
    td {
        div {
            css {
                marginLeft = 150.px
                fontSize = 15.px
                fontFamily = FontFamily.fantasy
                marginRight = 20.px
            }
            +formatTimestamp(dto.lastMoveTimestamp)
        }
    }
}

fun formatTimestamp(t: String?): String {
    if (t == null || t =="null" || t == "") {
        return ""
    }
    val d = Date(t)

    return "${d.getMonth()}-${d.getDate()}   ${d.getHours()}:${d.getMinutes()}"
}