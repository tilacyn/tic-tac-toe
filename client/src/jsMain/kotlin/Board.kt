import csstype.*
import csstype.LineStyle.Companion.solid
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
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.dom.html.TdHTMLAttributes

external interface BoardProps : Props {
    var boardModel: BoardModel
}

external interface BoardState : State {
    var boardModel: BoardModel
}


val Board = FC<BoardProps> { props ->
    val stateSetter = useState(props.boardModel)
    val model = stateSetter.component1()
    val setter = stateSetter.component2()
//    val n = model.
    div {
        css {
            padding = 5.px
            backgroundColor = rgb(8, 97, 22)
            color = rgb(56, 246, 137)
            width = 40.pct
            height = 50.px
            margin = Auto.auto
            textAlign = TextAlign.center
            fontSize = 24.px
        }
        +"Here is your playing board, you are playing X"
    }

    table {
        css {
//            width = 600.px
//            height = 600.px
            margin = Auto.auto
            marginTop = 200.px
            backgroundColor = rgb(52, 73, 94)
            color = rgb(255, 255, 255)
            borderStyle = solid
            borderWidth = 6.px
            borderColor = rgb(44, 62, 80)
            borderRadius = 10.px
//            display = Display.grid
//            gridTemplate =
        }
        model.array.forEachIndexed { i: Int, it: IntArray ->
            tr {
                it.forEachIndexed { j: Int, it: Int ->
                    if (it == 0) {
                        emptyCell(i, j, setter)
                    } else {
                        cell(it.toString())
                    }
                }
            }
        }

    }

}

fun ChildrenBuilder.emptyCell(i: Int, j: Int, setter: StateSetter<BoardModel>) {
    return td {
        onClick = {
            setter { model1 ->
                val model2 = BoardModel(
                    model1.array,
                    model1.playersMap
                )
                model2.array[i][j] = 1
                model2
            }
        }
        css(boxCss(true))
        +" "
    }

}

fun ChildrenBuilder.cell(s: String) {
    return td {
        css(boxCss())
        +s
    }
}

fun boxCss(useHover: Boolean = false): PropertiesBuilder.() -> Unit {
    return {
        margin = Auto.auto
        marginTop = 200.px
        backgroundColor = rgb(52, 73, 94)
        color = rgb(255, 255, 255)
        borderStyle = solid
        borderWidth = 6.px
        borderColor = rgb(44, 62, 80)
        borderRadius = 2.px
        fontWeight = FontWeight.bold
        fontSize = 4.em
        height=130.px
        width=130.px
//        fontFamily = csstype.FontFamily()
        justifyContent = JustifyContent.center
        alignItems = AlignItems.center
        textAlign = TextAlign.center
        if (useHover) {
            hover {
                backgroundColor = rgb(100, 200, 32)
            }
        }

//            display = Display.grid
//            gridTemplate =
    }
}