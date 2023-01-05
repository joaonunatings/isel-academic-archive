package pt.isel.pdm.chessroyale.chess.model.pieces

import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.PieceModel
import pt.isel.pdm.chessroyale.chess.model.utils.Position

/**
 * The King's piece logic with castle
 */
class King(army: Army) : PieceModel(army) {

    var canCastle = true
    override var pieceType: Piece = Piece.KING
    override var moves: ArrayList<Position> = ArrayList()

    override fun generatePossibleMoves(
        from: Position,
        board: HashMap<Position, PieceModel>
    ): ArrayList<Position> {
        moves.clear()
        val currPos = from.toRowCol()

        getCastle(from, board)

        checkBoundsAndCanMove(currPos, -1, 0, board)
        checkBoundsAndCanMove(currPos, -1, 1, board)
        checkBoundsAndCanMove(currPos, 0, 1, board)
        checkBoundsAndCanMove(currPos, 1, 1, board)
        checkBoundsAndCanMove(currPos, 1, 0, board)
        checkBoundsAndCanMove(currPos, 1, -1, board)
        checkBoundsAndCanMove(currPos, 0, -1, board)
        checkBoundsAndCanMove(currPos, -1, -1, board)

        return moves
    }

    private fun getCastle(from: Position, board: HashMap<Position, PieceModel>) {
        if (!canCastle) return
        val teamCol = from.toRowCol().second

        val queensideRookPos = Position.toPosition(1, teamCol)
        val queensideRook = board[queensideRookPos]
        val kingsideRookPos = Position.toPosition(8, teamCol)
        val kingsideRook = board[kingsideRookPos]

        if (queensideRook!!.pieceType == Piece.ROOK && queensideRook.army == army
            && !(queensideRook as Rook).hasMoved && nothingBetweenInRow(
                board,
                from.toRowCol().second,
                1,
                from.toRowCol().first
            )
        ) {
            moves.add(queensideRookPos)
        }
        if (kingsideRook!!.pieceType == Piece.ROOK && kingsideRook.army == army
            && !(kingsideRook as Rook).hasMoved && nothingBetweenInRow(
                board,
                from.toRowCol().second,
                from.toRowCol().first,
                8
            )
        ) {
            moves.add(kingsideRookPos)
        }

    }

    //TODO: functions  'nothingBetweenInRow' and 'canCastle' are too identical
    private fun nothingBetweenInRow(
        board: HashMap<Position, PieceModel>,
        row: Int,
        lesserCol: Int,
        greaterCol: Int
    ): Boolean {
        for (i in lesserCol.plus(1)..greaterCol.minus(1)) {
            if (board[Position.toPosition(i, row)]!!.javaClass != Empty(Army.NONE).javaClass) {
                return false
            }
        }
        return true
    }

    fun canCastle(from: Position, to: Position, board: HashMap<Position, PieceModel>): Boolean {
        if (!canCastle) {
            return false
        }

        val fromXY = from.toRowCol()
        val toXY = to.toRowCol()

        for (i in fromXY.first..toXY.first) {
            if (i != fromXY.first && i != toXY.first && board[Position.toPosition(
                    i,
                    fromXY.second
                )]!!.javaClass != Empty(Army.NONE).javaClass
            ) {
                return false
            }
        }
        return true
    }

    private fun checkBoundsAndCanMove(
        currPos: Pair<Int, Int>,
        x: Int,
        y: Int,
        board: HashMap<Position, PieceModel>
    ) {
        if (checkBounds(currPos.first + x) && checkBounds(currPos.second + y)) {
            val nextPos = Position.toPosition(currPos.first + x, currPos.second + y)
            val ahead = board[nextPos]!!
            if (ahead is Empty || isOpponent(ahead)) {
                moves.add(nextPos)
            }
        }
    }
}