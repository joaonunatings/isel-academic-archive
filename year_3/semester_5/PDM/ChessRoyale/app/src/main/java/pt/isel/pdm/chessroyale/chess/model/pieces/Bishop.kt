package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.BoardModel
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * The Bishop's piece logic
 */
class Bishop(army: Army) : PieceModel(army) {
    override var pieceType: Piece = Piece.BISHOP
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        moves.clear()

        val row: Int = from.toRowCol().first
        val column: Int = from.toRowCol().second

        var j = column + 1
        var i = row + 1
        while (j <= BoardModel.CHESS_BOARD_BOUNDARIES && i <= BoardModel.CHESS_BOARD_BOUNDARIES) {
            val nextPos = Position.toPosition(i, j)
            val ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
            j++
            i++
        }

        j = column - 1
        i = row + 1
        while (j >= 1 && i <= BoardModel.CHESS_BOARD_BOUNDARIES) {
            val nextPos = Position.toPosition(i, j)
            val ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
            j--
            i++
        }

        j = column - 1
        i = row - 1
        while (j >= 1 && i >= 1) {
            val nextPos = Position.toPosition(i, j)
            val ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
            j--
            i--
        }

        j = column + 1
        i = row - 1
        while (j <= BoardModel.CHESS_BOARD_BOUNDARIES && i >= 1) {
            val nextPos = Position.toPosition(i, j)
            val ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
            j++
            i--
        }

        return moves
    }
}