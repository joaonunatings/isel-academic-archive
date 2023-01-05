package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * The Knight's piece logic
 */
class Knight(army: Army) : PieceModel(army) {
    override var pieceType: Piece = Piece.KNIGHT
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        moves.clear()

        val row: Int = from.toRowCol().first
        val column: Int = from.toRowCol().second

        val offsets = arrayOf(
            intArrayOf(-2, 1),
            intArrayOf(-1, 2),
            intArrayOf(1, 2),
            intArrayOf(2, 1),
            intArrayOf(2, -1),
            intArrayOf(1, -2),
            intArrayOf(-1, -2),
            intArrayOf(-2, -1)
        )
        for (o in offsets) {

            if (checkBounds(row + o[0]) && checkBounds(column + o[1])) {
                val nextPos = Position.toPosition(row + o[0], column + o[1])
                val ahead = board[nextPos]!!

                if (ahead is Empty || isOpponent(ahead))
                    moves.add(nextPos)
            }
        }

        return moves
    }
}