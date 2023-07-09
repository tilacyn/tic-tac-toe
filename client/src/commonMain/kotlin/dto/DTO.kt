package dto

import kotlinx.serialization.*
import kotlin.time.TimeMark


@Serializable
// todo make array optional
data class BoardDTO(
    val id: String,
    val array: List<MutableList<String>>,
    val lastMoveUserID: String,
    val user2Symbol: Map<String, String>,
    val lastMoveTimestamp: String,
    val status: BoardStatus,
)

enum class BoardStatus {
    RUNNING,
    FINISHED
}

@Serializable
data class Move(val x: Int, val y: Int)

//@Serializable
//data class BoardDescriptionDTO(val id: String, val players: List<String>, val lastMoveTimestamp: String)