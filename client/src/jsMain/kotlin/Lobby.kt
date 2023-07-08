import csstype.px
import csstype.rgb
import react.FC
import react.Props
import emotion.react.css
import kotlinx.browser.window
import model.LobbyModel
import org.w3c.dom.Window
import org.w3c.xhr.XMLHttpRequest
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.useState

external interface LobbyProps : Props {
    var lobbyModel: LobbyModel
}

val Lobby = FC<LobbyProps> { props ->
    var model by useState(props.lobbyModel)
//    val n = model.
    div {
        css {
            padding = 5.px
            backgroundColor = rgb(8, 97, 22)
            color = rgb(56, 246, 137)
        }
        +"Lobby Component: text: ${model.n}"
    }
    input {
        css {
            marginTop = 5.px
            marginBottom = 5.px
            fontSize = 14.px
        }
        type = InputType.text
        value = name
        onChange = { event ->
            name = event.target.value
        }
    }

    div {
        id = "board1"
        onClick = {
            console.log("handling click (defined in react comp)")
            window.open("/board/1");
        }
        css {
            padding = 5.px
            backgroundColor = rgb(8, 97, 22)
            color = rgb(56, 246, 137)
            width = 100.px
            height = 100.px
        }
        + "Active Board"
    }
}