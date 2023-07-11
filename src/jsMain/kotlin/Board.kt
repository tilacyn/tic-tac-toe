import csstype.*
import csstype.LineStyle.Companion.solid
import dto.BoardDTO
import dto.BoardStatus
import emotion.react.css
import model.BoardModel
import model.boardData
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr

external interface BoardProps : Props {
    var boardModel: BoardModel
}


val Board = FC<BoardProps> { props ->
    val stateSetter = useState(props.boardModel)
    val model = stateSetter.component1()
    val setter = stateSetter.component2()
    boardSubscription.setStateSetter(setter)

    val prompt = createPrompt(model.dto)
    val moveAllowed: Boolean = model.dto.status != BoardStatus.FINISHED
            && model.dto.lastMoveUserID != boardData.userID


    promptSection(prompt)
    playersSection(model.dto)

    gameField(model, moveAllowed)

}

fun ChildrenBuilder.promptSection(prompt: String) {
    div {
        css {
            padding = 5.px
            backgroundColor = promptBackgroundColor
            color = promptTextColor
            width = 500.px
            height = 50.px
            margin = Auto.auto
            marginTop = 50.px
            textAlign = TextAlign.center
            fontSize = 24.px
            borderRadius = 9.px
            paddingTop = 10.px
        }
        +prompt
    }

}


fun ChildrenBuilder.playersSection(dto: BoardDTO) {
    div {
        css {
            position = Position.absolute
            width = 200.px
            fontSize = 24.px
            left = 50.px
            top = 50.px
        }
        div {
            css {
                color = rgb(30, 30, 30)
                marginLeft = 25.px
            }
            +"Players"
        }
        div {
            css {
                backgroundColor = promptBackgroundColor
                color = promptTextColor
                width = 200.px
                fontSize = 24.px
                borderRadius = 9.px
                marginTop = 25.px
            }
            dto.user2Symbol.forEach {
                div {
                    css {
                        paddingLeft = 25.px
                    }

                    +"${it.key}:"
                    span {
                        css {
                            color = cellColorMap[it.value] ?: NamedColor.white
                        }
                        +it.value
                    }
                }
            }
        }
    }
}

val promptTextColor = rgb(220, 220, 220)
val promptBackgroundColor = rgb(90, 90, 90)

fun ChildrenBuilder.gameField(model: BoardModel, moveAllowed: Boolean) {
    table {
        css {
            margin = Auto.auto
            marginTop = 80.px
            backgroundColor = rgb(52, 73, 94)
            color = rgb(255, 255, 255)
            borderStyle = solid
            borderWidth = 6.px
            borderColor = rgb(44, 62, 80)
            borderRadius = 10.px
        }
        model.dto.array!!.forEachIndexed { i: Int, it: List<String> ->
            tr {
                it.forEachIndexed { j: Int, it: String ->
                    if (it == "") {
                        emptyCell(i, j, model, moveAllowed)
                    } else {
                        cell(it)
                    }
                }
            }
        }

    }

}


fun ChildrenBuilder.emptyCell(
    i: Int,
    j: Int,
    board: BoardModel,
    moveAllowed: Boolean
) {
    return td {
        if (moveAllowed) {
            onClick = {
                board.move(i, j)
            }
        }
        css(boxCss(useHover = moveAllowed))
    }
}


val cellColorMap = mapOf(
    Pair("1", NamedColor.lightblue),
    Pair("2", NamedColor.lightgreen),
    Pair("3", NamedColor.lightcoral),
    Pair("4", NamedColor.lightcyan),
    Pair("5", NamedColor.lightpink),
)

fun ChildrenBuilder.cell(s: String) {
    val color = cellColorMap[s] ?: NamedColor.white
    return td {
        css(boxCss(color, false))
        +s
    }
}

fun boxCss(color1: NamedColor = NamedColor.white, useHover: Boolean): PropertiesBuilder.() -> Unit {
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
        fontSize = 2.8.em
        height = 65.px
        width = 65.px
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
        dto.status == BoardStatus.FINISHED && dto.winner == "" -> {
            "Game is Finished. Nobody won."
        }
        dto.status == BoardStatus.FINISHED && dto.winner != "" -> {
            "Game is Finished. Winner is ${dto.winner}!"
        }

        dto.lastMoveUserID == boardData.userID -> {
            "Last move was yours. Wait for another player to make a move."
        }

        dto.user2Symbol.containsKey(boardData.userID) -> {
            "You are playing \"${dto.user2Symbol[boardData.userID]}\". You can make a move."
        }

        else -> {
            "You can make a move."
        }
    }
}