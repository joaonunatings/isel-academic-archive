package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.BoardModel
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * The Rook's piece logic
 */
class Rook(army: Army) : PieceModel(army) {
    var hasMoved = false
    override var pieceType: Piece = Piece.ROOK
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        moves.clear()

        val row: Int = from.toRowCol().first
        val column: Int = from.toRowCol().second

        var nextPos: Position
        var ahead: PieceModel
        for (i in row + 1 until BoardModel.CHESS_BOARD_BOUNDARIES + 1) {
            nextPos = Position.toPosition(i, column)
            ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
        }
        for (i in row - 1 downTo 1) {
            nextPos = Position.toPosition(i, column)
            ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
        }
        for (i in column + 1 until BoardModel.CHESS_BOARD_BOUNDARIES + 1) {
            nextPos = Position.toPosition(row, i)
            ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
        }
        for (i in column - 1 downTo 1) {
            nextPos = Position.toPosition(row, i)
            ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)
            } else if (isOpponent(ahead)) {
                moves.add(nextPos)
                break
            } else {
                break
            }
        }

        return moves
    }

}