package pt.isel.pdm.chessroyale.chess.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.chess.view.Tile.Type


typealias TileTouchListener = (tile: Tile, row: Int, column: Int) -> Unit

/**
 * Custom view that implements a chess board.
 */
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {

    private val tiles: HashMap<Position, Tile> = HashMap(8 * 8)

    private val side = 8

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private fun createImageEntry(army: Army, piece: Piece, imageId: Int) =
        Pair(Pair(army, piece), VectorDrawableCompat.create(ctx.resources, imageId, null))

    private val piecesImages = mapOf(
        createImageEntry(Army.WHITE, Piece.PAWN, R.drawable.ic_white_pawn),
        createImageEntry(Army.WHITE, Piece.KNIGHT, R.drawable.ic_white_knight),
        createImageEntry(Army.WHITE, Piece.BISHOP, R.drawable.ic_white_bishop),
        createImageEntry(Army.WHITE, Piece.ROOK, R.drawable.ic_white_rook),
        createImageEntry(Army.WHITE, Piece.QUEEN, R.drawable.ic_white_queen),
        createImageEntry(Army.WHITE, Piece.KING, R.drawable.ic_white_king),
        createImageEntry(Army.BLACK, Piece.PAWN, R.drawable.ic_black_pawn),
        createImageEntry(Army.BLACK, Piece.KNIGHT, R.drawable.ic_black_knight),
        createImageEntry(Army.BLACK, Piece.BISHOP, R.drawable.ic_black_bishop),
        createImageEntry(Army.BLACK, Piece.ROOK, R.drawable.ic_black_rook),
        createImageEntry(Army.BLACK, Piece.QUEEN, R.drawable.ic_black_queen),
        createImageEntry(Army.BLACK, Piece.KING, R.drawable.ic_black_king),
    )

    private val piecesImagesRotated = mapOf(
        createImageEntry(Army.WHITE, Piece.PAWN, R.drawable.ic_white_pawn_inverted),
        createImageEntry(Army.WHITE, Piece.KNIGHT, R.drawable.ic_white_knight_inverted),
        createImageEntry(Army.WHITE, Piece.BISHOP, R.drawable.ic_white_bishop_inverted),
        createImageEntry(Army.WHITE, Piece.ROOK, R.drawable.ic_white_rook_inverted),
        createImageEntry(Army.WHITE, Piece.QUEEN, R.drawable.ic_white_queen_inverted),
        createImageEntry(Army.WHITE, Piece.KING, R.drawable.ic_white_king_inverted),
        createImageEntry(Army.BLACK, Piece.PAWN, R.drawable.ic_black_pawn_inverted),
        createImageEntry(Army.BLACK, Piece.KNIGHT, R.drawable.ic_black_knight_inverted),
        createImageEntry(Army.BLACK, Piece.BISHOP, R.drawable.ic_black_bishop_inverted),
        createImageEntry(Army.BLACK, Piece.ROOK, R.drawable.ic_black_rook_inverted),
        createImageEntry(Army.BLACK, Piece.QUEEN, R.drawable.ic_black_queen_inverted),
        createImageEntry(Army.BLACK, Piece.KING, R.drawable.ic_black_king_inverted),
    )

    init {
        rowCount = side
        columnCount = side
        repeat(side * side) {
            val row = it / side
            val column = it % side
            val tile = Tile(
                ctx,
                if ((row + column) % 2 == 0) Type.WHITE else Type.BLACK,
                side,
                piecesImages
            )
            tiles[invertX(Position.toPosition(column + 1, row + 1))] = tile
            tile.setOnClickListener { onTileClickedListener?.invoke(tile, row, column) }
            addView(tile)
        }
    }

    fun setTile(pos: Position, pair: Pair<Army, Piece>?) {
        tiles[pos]?.piece = pair
    }

    var onTileClickedListener: TileTouchListener? = null

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }

    private fun invertX(pos: Position): Position {
        val pair = pos.toRowCol()
        return Position.toPosition(pair.first, 9 - pair.second)
    }

    fun setSelected(pos: Position, select: Boolean) {
        tiles[pos]?.toSelect(select)
    }

    fun rotateBoard(army: Army) {
        rotation = if (army == Army.BLACK)
            180f
        else
            0f

        rotatePieces(army)
    }

    private fun rotatePieces(army: Army) {
        tiles.forEach { (_, tile) ->
            tile.images = if (army == Army.WHITE) {
                piecesImages
            } else {
                piecesImagesRotated
            }
            tile.invalidate()
        }
    }
}