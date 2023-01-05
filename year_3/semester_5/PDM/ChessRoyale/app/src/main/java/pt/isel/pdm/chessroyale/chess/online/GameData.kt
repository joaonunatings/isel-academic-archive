package pt.isel.pdm.chessroyale.chess.online

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data type used to represent the game state externally, that is, when the game state crosses
 * process boundaries and device boundaries.
 */
@Parcelize
data class GameData(
    val move: String?,
    val player: Boolean,
    val draw: Boolean?
) : Parcelable {
    companion object {
        fun builder() = mapOf(
            MOVE_STATE_KEY to "",
            PLAYER_STATE_KEY to false,
            DRAW_STATE_KEY to false
        )
    }
}

@Parcelize
data class MoveData(val move: String, val player: Boolean) : Parcelable {
    companion object {
        fun builder(lastMove: String, currPlayer: Boolean) =
            mapOf(MOVE_STATE_KEY to lastMove, PLAYER_STATE_KEY to currPlayer)
    }
}

@Parcelize
data class DrawData(val draw: Boolean?) : Parcelable {
    companion object {
        fun builder(drawValue: Boolean?) = mapOf(DRAW_STATE_KEY to drawValue)
    }
}