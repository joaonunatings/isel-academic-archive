package pt.isel.pdm.chessroyale.chess.puzzle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import pt.isel.pdm.chessroyale.chess.model.BoardModel
import pt.isel.pdm.chessroyale.chess.model.pieces.Pawn
import pt.isel.pdm.chessroyale.chess.model.utils.*
import pt.isel.pdm.chessroyale.chess.model.utils.Position.empty

private const val PUZZLE_ACTIVITY_MOVES_PLAYED = "PuzzleActivity.MovesPlayed"
private const val PUZZLE_ACTIVITY_PUZZLE_PGN = "PuzzleActivity.PuzzlePGN"
private const val PUZZLE_ACTIVITY_PUZZLE_SOL = "PuzzleActivity.PuzzleSol"

class PuzzleActivityViewModel(private val state: SavedStateHandle) : ViewModel() {

    val player: MutableLiveData<Army> by lazy {
        MutableLiveData<Army>()
    }

    private val movesPlayed: MutableLiveData<ArrayList<String>> = state.getLiveData(
        PUZZLE_ACTIVITY_MOVES_PLAYED
    )

    private val puzzlePGN: MutableLiveData<MutableList<String>> = state.getLiveData(
        PUZZLE_ACTIVITY_PUZZLE_PGN
    )

    val puzzleSolution: MutableLiveData<MutableList<String>> = state.getLiveData(
        PUZZLE_ACTIVITY_PUZZLE_SOL
    )

    val pieceMovedTo: MutableLiveData<ChessMove> by lazy {
        MutableLiveData<ChessMove>()
    }

    val chessBoardModel: MutableLiveData<BoardModel> by lazy {
        MutableLiveData<BoardModel>()
    }

    val solvedButton: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var chessBoard: HashMap<Position, PieceModel>? = chessBoardModel.value?.chessBoard

    init {

        player.value = Army.WHITE

        chessBoardModel.value = BoardModel(movesPlayed)

        if (chessBoard == null) chessBoard = chessBoardModel.value!!.chessBoard

        if (movesPlayed.value == null) state.set(PUZZLE_ACTIVITY_MOVES_PLAYED, ArrayList<String>())

        if (movesPlayed.value!!.isNotEmpty()) resettleMoves()

        if (solvedButton.value == null) solvedButton.value = true
    }

    fun cycleBoard() {
        chessBoard!!.forEach {
            pieceMovedTo.value = ChessMove(empty, it.key)
        }
    }

    fun nextMove(from: Position, to: Position) {
        chessBoardModel.value!!.movePiece(from, to)
        pieceMovedTo.value = ChessMove(from, to)
    }

    fun processChessMoves(chessPGN: String) {
        if (puzzlePGN.value == null) state.set(
            PUZZLE_ACTIVITY_PUZZLE_PGN,
            chessPGN.split(" ").toMutableList()
        )

        if (puzzlePGN.value != null && puzzlePGN.value!!.isNotEmpty()) {
            for (i in puzzlePGN.value!!) {
                makeMove(i)
            }
        }

        puzzlePGN.value = listOf<String>().toMutableList()
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

    private fun makeMove(move: String) {
        val pieceType: PieceModel
        val toPos: Position

        //Find the piece that's to be moved and the position Enum to be used
        when {
            move[0].isLowerCase() -> {
                pieceType = Pawn(Army.NONE)
                toPos = convertPositionPGNPawn(move)
                chessBoardModel.value!!.tryMove(toPos, pieceType, player.value!!)
            }
            move[0] == 'O' -> { //Castling  Kingside -> O-O or Queenside -> O-O-O
                val isKingside: Boolean = move.split("-").size == 2
                chessBoardModel.value!!.tryCastling(player.value!!, isKingside)
            }
            else -> {
                pieceType = convertPiecePGN(move[0])
                toPos = convertPositionPGN(move.substring(1))
                chessBoardModel.value!!.tryMove(toPos, pieceType, player.value!!)
            }
        }

        updatePlayerArmy()

    }

    fun updatePlayerArmy() {
        player.value = if (player.value == Army.WHITE) Army.BLACK else Army.WHITE
    }

    private fun convertPositionPGNPawn(pos: String): Position {
        var position = pos
        if (position[1] == 'x') {
            position = pos.substring(2, 4)
            chessBoardModel.value!!.knownCol = pos[0]
        }
        return Position.valueOf(position.substring(0, 2))
    }

    private fun convertPositionPGN(pos: String): Position {
        var position = pos
        if (position[0] == 'x') {
            position = pos.substring(1, 3)
        } else if (pos.length == 3 && pos[2] != '+') {
            position = pos.substring(1, 3)
            chessBoardModel.value!!.knownCol = pos[0]
        } else if (pos.length == 4 && position[1] == 'x' && pos[2] != '+') {
            position = pos.substring(2, 4)
            chessBoardModel.value!!.knownCol = pos[0]
        }
        return Position.valueOf(position.substring(0, 2))
    }

    private fun convertPiecePGN(piece: Char): PieceModel {
        return PieceLetter.valueOf(piece.toString()).pieceType
    }

    fun setPuzzleSolution(solutionStr: Array<String>) {
        if (puzzleSolution.value == null) {
            state.set(PUZZLE_ACTIVITY_PUZZLE_SOL, solutionStr.toMutableList())
        }
    }
}