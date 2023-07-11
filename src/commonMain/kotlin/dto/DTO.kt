package dto

import kotlinx.serialization.Serializable


@Serializable
data class BoardDTO(
    val id: String,
    val array: List<MutableList<String>>?,
    val lastMoveUserID: String,
    val user2Symbol: Map<String, String>,
    val lastMoveTimestamp: String?,
    val status: BoardStatus,
    val winner: String,
)

enum class BoardStatus {
    RUNNING,
    FINISHED
}

@Serializable
data class Move(val x: Int, val y: Int)
