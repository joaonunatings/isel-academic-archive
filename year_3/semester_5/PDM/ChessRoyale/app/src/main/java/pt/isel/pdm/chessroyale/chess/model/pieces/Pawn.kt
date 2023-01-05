package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * The Pawn's piece logic
 */
class Pawn(army: Army) : PieceModel(army) {

    override var pieceType: Piece = Piece.PAWN
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        moves.clear()

        val color: Boolean = board[from]!!.army.dir == 1
        val dx = if (color) 1 else -1
        val currPos = from.toRowCol()
        var nextPos: Position
        var ahead: PieceModel

        if (checkBounds(currPos.second + dx)) {
            nextPos = Position.toPosition(currPos.first, currPos.second + dx)
            ahead = board[nextPos]!!
            if (ahead is Empty) {
                moves.add(nextPos)

                if (currPos.second == 7 && !color) {
                    nextPos = Position.toPosition(currPos.first, currPos.second + dx - 1)
                    ahead = board[nextPos]!!

                    if (ahead is Empty) moves.add(nextPos)

                } else if (currPos.second == 2 && color) {
                    nextPos = Position.toPosition(currPos.first, currPos.second + dx + 1)
                    ahead = board[nextPos]!!

                    if (ahead is Empty) moves.add(nextPos)
                }
            }
        }

        if (checkBounds(currPos.second + dx) && checkBounds(currPos.first - 1)) {
            nextPos = Position.toPosition(currPos.first - 1, currPos.second + dx)
            ahead = board[nextPos]!!

            if (ahead !is Empty && isOpponent(ahead)) moves.add(nextPos)
        }

        if (checkBounds(currPos.second + dx) && checkBounds(currPos.first + 1)) {
            nextPos = Position.toPosition(currPos.first + 1, currPos.second + dx)
            ahead = board[nextPos]!!

            if (ahead !is Empty && isOpponent(ahead)) moves.add(nextPos)
        }
        return moves
    }
}