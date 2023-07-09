import csstype.*
import csstype.LineStyle.Companion.solid
import dto.BoardDTO
import dto.BoardStatus
import emotion.css.css
import emotion.react.css
import kotlinx.browser.window
import model.BoardModel
import model.LobbyModel
import org.w3c.dom.HTMLTableCellElement
import org.w3c.dom.Window
import org.w3c.xhr.XMLHttpRequest
import react.*
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.dom.html.TdHTMLAttributes

external interface BoardProps : Props {
    var boardModel: BoardModel
}



val Board = FC<BoardProps> { props ->
    val stateSetter = useState(props.boardModel)
    val model = stateSetter.component1()
    val setter = stateSetter.component2()
    boardSubscription.setStateSetter(setter)

    val prompt = createPrompt(model.dto)
    val moveAllowed: Boolean = model.dto.status == BoardStatus.FINISHED
            || model.dto.lastMoveUserID == userID

    div {
        css {
            padding = 5.px
            backgroundColor = rgb(90, 90, 90)
            color = rgb(180, 180, 180)
            width = 40.pct
            height = 50.px
            margin = Auto.auto
            textAlign = TextAlign.center
            fontSize = 24.px
            borderRadius = 9.px
        }
        +prompt
    }

    playersSection(model.dto)

    table {
        css {
            margin = Auto.auto
            marginTop = 100.px
            backgroundColor = rgb(52, 73, 94)
            color = rgb(255, 255, 255)
            borderStyle = solid
            borderWidth = 6.px
            borderColor = rgb(44, 62, 80)
            borderRadius = 10.px
        }
        model.dto.array.forEachIndexed { i: Int, it: List<String> ->
            tr {
                it.forEachIndexed { j: Int, it: String ->
                    if (it == "") {
                        emptyCell(i, j, model, setter, moveAllowed)
                    } else {
                        cell(it)
                    }
                }
            }
        }

    }

}

fun ChildrenBuilder.playersSection(dto: BoardDTO) {
    div {
        css {
            position = Position.absolute
//                padding = 5.px
            backgroundColor = rgb(90, 90, 90)
            color = rgb(180, 180, 180)
            width = 200.px
//            height = 50.px
//            margin = Auto.auto
//            textAlign = TextAlign.center
            fontSize = 24.px
            borderRadius = 9.px
            left = 50.px
            top = 300.px
        }
        div {
            css {
//                padding = 5.px
//                width = 100.px
                marginLeft = 20.px
            }
            +"Players"
        }
        dto.user2Symbol.forEach {
            div {
                +"${it.key}: ${it.value}"
            }
        }
    }
}

fun ChildrenBuilder.emptyCell(i: Int, j: Int, board: BoardModel, setter: StateSetter<BoardModel>, moveAllowed: Boolean) {
    return td {
        onClick = {
            val newBoard = board.copy()
            board.move(i, j).then { newModel ->
                console.log(newModel)
                console.log(newBoard)
                console.log("processing promise (then)")
                setter {
                    newModel
                }
            }
        }
        css(boxCss(useHover = moveAllowed))
    }

}


val cellColorMap = mapOf(
    Pair("1", NamedColor.gray),
    Pair("2", NamedColor.blue),
    Pair("3", NamedColor.red),
    Pair("4", NamedColor.green),
    Pair("5", NamedColor.yellow),
)

fun ChildrenBuilder.cell(s: String) {
    val color = cellColorMap[s] ?: NamedColor.white
    return td {
        css(boxCss(color))
        +s
    }
}

fun boxCss(color1: NamedColor = NamedColor.white, useHover: Boolean = false): PropertiesBuilder.() -> Unit {
    return {
        margin = Auto.auto
        marginTop = 200.px
        backgroundColor = rgb(52, 73, 94)
        color = color1
        borderStyle = solid
        borderWidth = 6.px
        borderColor = rgb(44, 62, 80)
        borderRadius = 2.px
        fontWeight = FontWeight.bold
        fontSize = 3.em
        height = 80.px
        width = 80.px
        justifyContent = JustifyContent.center
        alignItems = AlignItems.center
        textAlign = TextAlign.center
        if (useHover) {
            hover {
                backgroundColor = rgb(100, 200, 32)
            }
        }
    }
}



fun createPrompt(dto: BoardDTO): String {
    return when {
        dto.status == BoardStatus.FINISHED -> {
            "Game is Finished"
        }

        dto.lastMoveUserID == userID -> {
            "Last move was yours. Wait for another player to make a move"
        }

        dto.user2Symbol.containsKey(userID) -> {
            "You are playing \"${dto.user2Symbol[userID]}\". You can make a move"
        }

        else -> {
            "You can make a move"
        }
    }
}