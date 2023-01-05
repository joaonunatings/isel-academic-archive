package pt.isel.pdm.chessroyale.chess.model.utils

// Represents each chess piece
abstract class PieceModel(var army: Army) {

    abstract val pieceType: Piece
    protected abstract var moves: ArrayList<Position>

    abstract fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position>

    fun canMove(to: Position): Boolean {
        if (moves.contains(to)) return true
        return false
    }

    fun canMoveWithGenerate(
        from: Position,
        to: Position,
        board: HashMap<Position, PieceModel>
    ): Boolean {
        generatePossibleMoves(from, board)
        if (moves.contains(to)) return true
        return false
    }

    protected fun isOpponent(piece: PieceModel): Boolean {
        return piece.army != this.army
    }

    protected fun checkBounds(toCheck: Int): Boolean = toCheck in 1..8
}