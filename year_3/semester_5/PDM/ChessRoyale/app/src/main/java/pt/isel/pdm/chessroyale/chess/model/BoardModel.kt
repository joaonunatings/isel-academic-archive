package pt.isel.pdm.chessroyale.chess.model

import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chessroyale.chess.model.pieces.*
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Army.*
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.chess.model.utils.Position.*

/**
 * The ChessBoard model which provides logic for chess's backend
 */
class BoardModel(private val movesPlayed: MutableLiveData<ArrayList<String>>) {

    companion object {
        const val CHESS_BOARD_BOUNDARIES = 8
        const val CHESS_BOARD_SIZE = CHESS_BOARD_BOUNDARIES * CHESS_BOARD_BOUNDARIES
    }

    var chessBoard: HashMap<Position, PieceModel> = HashMap(CHESS_BOARD_SIZE)
    var knownCol: Char = ' '

    init {
        initializeBoard()
    }

    private fun initializeBoard() {
        chessBoard = HashMap(CHESS_BOARD_SIZE)

        chessBoard[a1] = Rook(WHITE)
        chessBoard[b1] = Knight(WHITE)
        chessBoard[c1] = Bishop(WHITE)
        chessBoard[d1] = Queen(WHITE)
        chessBoard[e1] = King(WHITE)
        chessBoard[f1] = Bishop(WHITE)
        chessBoard[g1] = Knight(WHITE)
        chessBoard[h1] = Rook(WHITE)
        fillRow(WHITE)

        chessBoard[a8] = Rook(BLACK)
        chessBoard[b8] = Knight(BLACK)
        chessBoard[c8] = Bishop(BLACK)
        chessBoard[d8] = Queen(BLACK)
        chessBoard[e8] = King(BLACK)
        chessBoard[f8] = Bishop(BLACK)
        chessBoard[g8] = Knight(BLACK)
        chessBoard[h8] = Rook(BLACK)
        fillRow(BLACK)

        Position.values().forEach {
            if (!chessBoard.containsKey(it)) chessBoard[it] = Empty(NONE)
        }
        chessBoard.remove(empty)
    }

    fun setPiece(pos: Position, piece: PieceModel) {
        chessBoard[pos] = piece
        when (piece) {
            is King -> piece.canCastle = false
            is Rook -> piece.hasMoved = true
        }
    }

    fun removePiece(pos: Position) {
        setPiece(pos, Empty(NONE))
    }

    fun tryMove(toPos: Position, pieceType: PieceModel, player: Army) {

        val filteredPiece = chessBoard.entries.first {
            it.value.javaClass == pieceType.javaClass && it.value.army == player && (if (knownCol != ' ') it.key.name[0] == knownCol else true) && it.value.canMoveWithGenerate(
                it.key,
                toPos,
                chessBoard
            )
        } //Resetting the known column
        knownCol = ' '

        movePiece(filteredPiece.key, toPos)
    }

    fun movePiece(from: Position, to: Position) {
        val piece: PieceModel = chessBoard[from]!!
        setPiece(to, piece)
        removePiece(from)
        movesPlayed.value?.add("${from}${to}")
    }

    /**
     * @return array.get(0) -> King's Position Destination; array.get(1) -> Rook's Position Destination.
     */
    fun tryCastling(player: Army, isKingside: Boolean): Array<Position> {

        val filteredRook = chessBoard.entries.filter {
            it.value.javaClass == Rook(NONE).javaClass && it.value.army == player && (if (isKingside) it.key.toRowCol().first != 1 else it.key.toRowCol().first == 1) && !(it.value as Rook).hasMoved
        }

        if (filteredRook.isEmpty())
            throw IllegalArgumentException("No rook for suggested castling")

        val filteredKing = chessBoard.entries.first {
            it.value.javaClass == King(NONE).javaClass && it.value.army == player && (it.value as King).canCastle(
                it.key,
                filteredRook.first().key,
                chessBoard
            )
        }

        return if (isKingside)
            kingsideCastling(filteredKing, filteredRook.first())
        else
            queensideCastling(filteredKing, filteredRook.first())
    }

    private fun queensideCastling(
        filteredKing: MutableMap.MutableEntry<Position, PieceModel>,
        filteredRook: MutableMap.MutableEntry<Position, PieceModel>
    ): Array<Position> {
        return castling(filteredKing, filteredRook, 2, 3)
    }

    private fun kingsideCastling(
        filteredKing: MutableMap.MutableEntry<Position, PieceModel>,
        filteredRook: MutableMap.MutableEntry<Position, PieceModel>
    ): Array<Position> {
        return castling(filteredKing, filteredRook, -1, -2)
    }

    // Kingside ->  KING = rook-1  ROOK = rook-2
    // Queenside -> KING = rook+2  ROOK = rook+3
    private fun castling(
        filteredKing: MutableMap.MutableEntry<Position, PieceModel>,
        filteredRook: MutableMap.MutableEntry<Position, PieceModel>,
        kingOffset: Int,
        rookOffset: Int
    ): Array<Position> {
        val toPairXY = filteredRook.key.toRowCol()
        var to: Position = Position.toPosition(toPairXY.first + kingOffset, toPairXY.second)
        movePiece(filteredKing.key, to)
        val kingAndRook = arrayOf(to, empty)


        to = Position.toPosition(toPairXY.first + rookOffset, toPairXY.second)
        movePiece(filteredRook.key, to)
        kingAndRook[1] = to

        return kingAndRook
    }

    private fun fillRow(army: Army) {
        var line = 2
        if (army == BLACK) line = 7

        var row = 'a'
        for (i in 1 until CHESS_BOARD_BOUNDARIES + 1) {
            chessBoard[Position.valueOf("$row$line")] = Pawn(army)
            row++
        }
    }

    fun emptyPair() = Pair(NONE, Piece.EMPTY)
}
