package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * Empty Piece logic
 */
class Empty(army: Army) : PieceModel(army) {
    override var pieceType: Piece = Piece.EMPTY
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        throw Exception("Should never get here! Can't generate moves for Empty piece.")
    }
}