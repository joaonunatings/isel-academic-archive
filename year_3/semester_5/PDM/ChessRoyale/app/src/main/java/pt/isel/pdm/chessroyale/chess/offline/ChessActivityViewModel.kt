package pt.isel.pdm.chessroyale.chess.offline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import pt.isel.pdm.chessroyale.chess.model.BoardModel
import pt.isel.pdm.chessroyale.chess.model.utils.*
import pt.isel.pdm.chessroyale.chess.model.utils.Position.empty

private const val PUZZLE_ACTIVITY_MOVES_PLAYED = "PuzzleActivity.MovesPlayed"

class ChessActivityViewModel(state: SavedStateHandle) : ViewModel() {

    val player: MutableLiveData<Army> by lazy {
        MutableLiveData<Army>()
    }

    private val movesPlayed: MutableLiveData<ArrayList<String>> = state.getLiveData(
        PUZZLE_ACTIVITY_MOVES_PLAYED
    )

    val pieceMovedTo: MutableLiveData<ChessMove> by lazy {
        MutableLiveData<ChessMove>()
    }

    val chessBoardModel: MutableLiveData<BoardModel> by lazy {
        MutableLiveData<BoardModel>()
    }

    var chessBoard: HashMap<Position, PieceModel>? = chessBoardModel.value?.chessBoard

    init {

        player.value = Army.WHITE

        chessBoardModel.value = BoardModel(movesPlayed)

        if (chessBoard == null) chessBoard = chessBoardModel.value!!.chessBoard

        if (movesPlayed.value == null) state.set(PUZZLE_ACTIVITY_MOVES_PLAYED, ArrayList<String>())

        if (movesPlayed.value!!.isNotEmpty()) resettleMoves()
    }

    fun cycleBoard() {
        chessBoard!!.forEach {
            pieceMovedTo.value = ChessMove(empty, it.key)
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

        } else {
            chessBoardModel.value!!.movePiece(from, to)

            pieceMovedTo.value = ChessMove(from, to)
        }


    }

    private fun calculateKingOrQueenSideCastling(to: Position): Boolean {
        return to.toRowCol().first != 1
    }

    private fun resettleMoves() {
        movesPlayed.value!!.forEach {
            val from = Position.valueOf(it.substring(0, 2))
            val to = Position.valueOf(it.substring(2, 4))

            nextMove(from, to)
            updatePlayerArmy()
        }
    }

    fun updatePlayerArmy() {
        player.value = if (player.value == Army.WHITE) Army.BLACK else Army.WHITE
    }
}