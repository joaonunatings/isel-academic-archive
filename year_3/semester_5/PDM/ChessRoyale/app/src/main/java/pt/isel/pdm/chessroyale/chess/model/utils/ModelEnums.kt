package pt.isel.pdm.chessroyale.chess.model.utils

import pt.isel.pdm.chessroyale.chess.model.pieces.*
import pt.isel.pdm.chessroyale.chess.model.utils.Army.NONE

/**
 * All Model enums
 */
enum class Army(val dir: Int) {
    WHITE(1), BLACK(-1), NONE(0)
}

enum class Piece {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING, EMPTY
}

enum class PieceLetter(val pieceType: PieceModel) {
    K(King(NONE)), Q(Queen(NONE)), B(Bishop(NONE)), N(Knight(NONE)), R(Rook(NONE)), P(Pawn(NONE))
}