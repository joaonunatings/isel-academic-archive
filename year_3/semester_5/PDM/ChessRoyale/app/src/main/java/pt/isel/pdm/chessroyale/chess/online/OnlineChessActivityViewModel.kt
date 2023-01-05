package pt.isel.pdm.chessroyale.chess.online

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chessroyale.chess.model.BoardModel
import pt.isel.pdm.chessroyale.chess.model.utils.*
import pt.isel.pdm.chessroyale.common.ChessRoyaleApplication


/**
 * The Game screen view model
 */

const val ONLINE_CHESS_ACTIVITY_MOVES_PLAYED = "OnlineChessActivity.MovesPlayed"
const val ONLINE_CHESS_ACTIVITY_ONLINE_PLAYER = "OnlineChessActivity.OnlinePlayer"
const val ONLINE_CHESS_ACTIVITY_CHALLENGE = "OnlineChessActivity.ChallengeId"
const val ONLINE_CHESS_ACTIVITY_LAST_MOVE = "OnlineChessActivity.LastMove"


class OnlineChessActivityViewModel(app: Application, state: SavedStateHandle) :
    AndroidViewModel(app) {

    private val movesPlayed: MutableLiveData<ArrayList<String>> = state.getLiveData(
        ONLINE_CHESS_ACTIVITY_MOVES_PLAYED
    )
    val lastMove: MutableLiveData<String> = state.getLiveData(ONLINE_CHESS_ACTIVITY_LAST_MOVE)
    private val onlinePlayer: MutableLiveData<Boolean> = state.getLiveData(
        ONLINE_CHESS_ACTIVITY_ONLINE_PLAYER
    )
    private val challengeId: MutableLiveData<String> = state.getLiveData(
        ONLINE_CHESS_ACTIVITY_CHALLENGE
    )

    val draw: MutableLiveData<Boolean?> by lazy {
        MutableLiveData<Boolean?>()
    }

    val localDraw: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val player: MutableLiveData<Army> by lazy {
        MutableLiveData<Army>()
    }

    val pieceMovedTo: MutableLiveData<ChessMove> by lazy {
        MutableLiveData<ChessMove>()
    }

    val chessBoardModel: MutableLiveData<BoardModel> by lazy {
        MutableLiveData<BoardModel>()
    }

    var chessBoard: HashMap<Position, PieceModel>? = chessBoardModel.value?.chessBoard

    init {
        if (localDraw.value == null) localDraw.value = false

        player.value = Army.WHITE

        chessBoardModel.value = BoardModel(movesPlayed)

        if (chessBoard == null) chessBoard = chessBoardModel.value!!.chessBoard

        if (movesPlayed.value == null) state.set(
            ONLINE_CHESS_ACTIVITY_MOVES_PLAYED,
            ArrayList<String>()
        )

        if (movesPlayed.value!!.isNotEmpty()) resettleMoves()
    }

    val gameSubscription = getApplication<ChessRoyaleApplication>()
        .gamesRepository.subscribeToGameStateChanges(
            challengeId = challengeId.value!!,
            onMoveDataChange = {
                if (it.player != playerToBool()) {
                    lastMove.value = it.move
                    setCurrentPlayer(it.player)
                }
            },
            onDrawDataChange = {
                draw.value = it.draw
            },
            onDisconnect = {
                draw.value = null
            },
            onSubscriptionError = {
            }
        )

    private fun setCurrentPlayer(currentPlayer: Boolean) {
        player.value = if (currentPlayer) Army.BLACK else Army.WHITE
    }

    fun cycleBoard() {
        chessBoard!!.forEach {
            pieceMovedTo.value = ChessMove(Position.empty, it.key)
        }
    }

    fun nextMove(from: Position, to: Position) {
        if (chessBoard!![from]!!.pieceType == Piece.KING
            && chessBoard!![to]!!.pieceType == Piece.ROOK
            && chessBoard!![from]!!.army == chessBoard!![to]!!.army
        ) {
            val isKingSide: Boolean = calculateKingOrQueenSideCastling(to)
            val kingAndRookDestination =
                chessBoardModel.value!!.tryCastling(player.value!!, isKingSide)
            val king = kingAndRookDestination[0]
            val rook = kingAndRookDestination[1]

            pieceMovedTo.value = ChessMove(from, king)
            pieceMovedTo.value = ChessMove(to, rook)
            if (getPlayer() == player.value) {
                lastMove.value = from.name + king.name
            }

        } else {
            chessBoardModel.value!!.movePiece(from, to)

            pieceMovedTo.value = ChessMove(from, to)
            if (getPlayer() == player.value)
                lastMove.value = from.name + to.name
        }
    }

    private fun calculateKingOrQueenSideCastling(to: Position): Boolean {
        return to.toRowCol().first != 1
    }

    private fun resettleMoves() {
        movesPlayed.value!!.forEach {
            val from = Position.valueOf(it.substring(0, 2))
            val to = Position.valueOf(it.substring(2, 4))

            chessBoardModel.value!!.setPiece(to, chessBoard!![from]!!)
            chessBoardModel.value!!.removePiece(from)
            pieceMovedTo.value = ChessMove(from, to)
            updatePlayerArmy()
        }
    }

    fun updatePlayerArmy() {
        player.value = if (player.value == Army.WHITE) Army.BLACK else Army.WHITE
    }

    fun getPlayer(): Army {
        return if (onlinePlayer.value!!) Army.BLACK else Army.WHITE
    }

    fun getLastMove(): String {
        return lastMove.value!!
    }

    private fun playerToBool(): Boolean {
        return player.value!! != Army.WHITE
    }

    fun setOnlinePlayer(player: Int) {
        when (player) {
            0 -> {
                onlinePlayer.value = false
            }
            1 -> {
                onlinePlayer.value = true
            }
            else -> {
                throw IllegalArgumentException("Mandatory extra onlinePlayer not present")
            }
        }
    }

    fun setChallengeId(id: String?) {
        if (id == null) throw IllegalArgumentException("Mandatory Challenge ID")
        challengeId.value = id
    }

    private fun deleteGame() {
        getApplication<ChessRoyaleApplication>().gamesRepository.deleteGame(
            challengeId = challengeId.value!!,
            onComplete = { }
        )
        gameSubscription.remove()
    }

    override fun onCleared() {
        super.onCleared()
        deleteGame()
    }

    fun updateDraw(draw: Boolean?) {
        getApplication<ChessRoyaleApplication>()
            .gamesRepository.updateDraw(challengeId.value!!, draw) { }
    }

    fun updateMove() {
        getApplication<ChessRoyaleApplication>()
            .gamesRepository.updateMove(challengeId.value!!, lastMove.value!!, playerToBool()) { }
    }

    fun removeGame() {
        getApplication<ChessRoyaleApplication>()
            .gamesRepository.deleteGame(
                challengeId = challengeId.value!!
            ) { }
    }

}