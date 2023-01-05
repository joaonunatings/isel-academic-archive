package pt.isel.pdm.chessroyale.chess.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.Piece

/**
 * Custom view that implements a chess board tile.
 * Tiles are either black or white and they can be empty or occupied by a chess piece.
 *
 * @property type           The tile's type (i.e. black or white)
 * @property tilesPerSide   The number of tiles in each side of the chess board
 *
 */
@SuppressLint("ViewConstructor")
class Tile(
    private val ctx: Context,
    private val type: Type,
    private val tilesPerSide: Int,
    var images: Map<Pair<Army, Piece>, VectorDrawableCompat?>,
    initialPiece: Pair<Army, Piece>? = null,
) : View(ctx) {

    private var toSelect: Boolean = false

    fun toSelect(select: Boolean) {
        toSelect = select
        invalidate()
    }

    var piece: Pair<Army, Piece>? = initialPiece
        set(value) {
            field = value
            invalidate()
        }

    enum class Type { WHITE, BLACK }

    private val brush = Paint().apply {
        color = ctx.resources.getColor(
            if (type == Type.WHITE) R.color.chess_board_white else R.color.chess_board_black,
            null
        )
        style = Paint.Style.FILL_AND_STROKE
    }

    private val brushSelect = Paint().apply {
        color = Color.rgb(0, 176, 9)
        style = Paint.Style.FILL_AND_STROKE
        alpha = 200
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = Integer.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(side / tilesPerSide, side / tilesPerSide)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), brush)
        if (piece != null) {
            images[piece]?.apply {
                val padding = 8
                setBounds(padding, padding, width - padding, height - padding)
                draw(canvas)
            }
        }
        if (toSelect) {
            canvas.drawCircle(
                width.toFloat() / 2,
                width.toFloat() / 2,
                width.toFloat() / 5,
                brushSelect
            )
        }
    }
}