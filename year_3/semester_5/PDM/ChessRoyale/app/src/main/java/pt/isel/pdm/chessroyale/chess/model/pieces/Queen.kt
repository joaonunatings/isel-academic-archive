package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * The Queen's piece logic
 */
class Queen(army: Army) : PieceModel(army) {
    override var pieceType: Piece = Piece.QUEEN
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        moves.clear()

        val pieces = arrayOf(Rook(army), Bishop(army))
        for (piece in pieces) {
            board[from] = piece
            val positions = piece.generatePossibleMoves(from, board)
            board[from] = this
            moves.addAll(positions)
        }

        return moves
    }
}