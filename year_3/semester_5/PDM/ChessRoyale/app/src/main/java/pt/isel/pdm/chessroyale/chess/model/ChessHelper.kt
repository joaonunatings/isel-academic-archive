package pt.isel.pdm.chessroyale.chess.model

import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.chess.view.BoardView
import kotlin.math.absoluteValue

/**
 * Simply the DRY principle
 */
class ChessHelper(row: Int, column: Int, private val boardView: BoardView) {

    var pos: Position = Position.toPosition(viewToModelPosX(column), viewToModelPosY(row))
    var moves: ArrayList<Position>? = null

    fun tryShowPossibleMoves(
        player: Army,
        chessBoard: HashMap<Position, PieceModel>
    ): Boolean {

        if (player == chessBoard[pos]!!.army) {
            moves = chessBoard[pos]!!.generatePossibleMoves(pos, chessBoard)
            setSelectedPos(moves, true)
            return true

        }
        return false
    }

    fun move(
        move: Position,
        chessBoard: HashMap<Position, PieceModel>,
        nextMove: (Position, Position) -> Unit,
        updatePlayerArmy: () -> Unit
    ): Boolean {

        var moved = false
        val movePos = chessBoard[move]!!
        moves = movePos.generatePossibleMoves(move, chessBoard)


        pos = generateCanMoveValue(move, movePos)
        if (moves!!.isNotEmpty() && movePos.canMove(pos)) {
            nextMove(move, pos)
            updatePlayerArmy()
            moved = true
        }

        setSelectedPos(moves, false)
        return moved
    }

    private fun generateCanMoveValue(move: Position, movePos: PieceModel): Position {
        return if (movePos.pieceType == Piece.KING && pos.toRowCol().first.minus(move.toRowCol().first).absoluteValue > 1) {
            if (pos.toRowCol().first == 1) {
                Position.toPosition(pos.toRowCol().first + 2, pos.toRowCol().second)
            } else {
                Position.toPosition(pos.toRowCol().first + 1, pos.toRowCol().second)
            }
        } else {
            pos
        }
    }

    companion object {
        fun viewToModelPosX(x: Int) = x + 1

        fun viewToModelPosY(y: Int) = 7 - y + 1
    }

    private fun setSelectedPos(moves: ArrayList<Position>?, b: Boolean) {
        moves?.forEach {
            boardView.setSelected(it, b)
        }
    }

    fun setSelectedPos(b: Boolean) {
        setSelectedPos(moves, b)
    }

}