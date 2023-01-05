package pt.isel.pdm.chessroyale.chess.offline

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.model.ChessHelper
import pt.isel.pdm.chessroyale.chess.model.ChessHelper.Companion.viewToModelPosX
import pt.isel.pdm.chessroyale.chess.model.ChessHelper.Companion.viewToModelPosY
import pt.isel.pdm.chessroyale.chess.model.pieces.Empty
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.ChessMove
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.chess.model.utils.Position.empty
import pt.isel.pdm.chessroyale.chess.view.Tile
import pt.isel.pdm.chessroyale.chessroyale.MainActivity.Companion.BLACK_WON
import pt.isel.pdm.chessroyale.chessroyale.MainActivity.Companion.WHITE_WON
import pt.isel.pdm.chessroyale.databinding.ActivityChessBinding

/**
 * Locally played game of chess, each turn the board moves for the other player to play
 */
class ChessActivity : AppCompatActivity() {

    private val chessViewModel: ChessActivityViewModel by viewModels()

    private val currPlayer: ImageView by lazy {
        findViewById(R.id.currPlayer)
    }

    private val binding by lazy {
        ActivityChessBinding.inflate(layoutInflater)
    }

    private val chessBoard by lazy {
        chessViewModel.chessBoard!!
    }

    private val boardView by lazy {
        binding.boardView
    }

    private val boardModel by lazy {
        chessViewModel.chessBoardModel.value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Creates an observer to notify the view of the board's changes
        val observeChessMove: Observer<ChessMove> = Observer<ChessMove> {
            boardView.setTile(it.from, boardModel.emptyPair())

            boardView.setTile(it.to, Pair(chessBoard[it.to]!!.army, chessBoard[it.to]!!.pieceType))
        }

        val observePlayer: Observer<Army> = Observer<Army> {
            if (chessViewModel.player.value == Army.BLACK)
                currPlayer.setImageResource(R.drawable.ic_black_king)
            else
                currPlayer.setImageResource(R.drawable.ic_white_king)
            boardView.rotateBoard(chessViewModel.player.value!!)
        }

        //Makes the previous observer notify the view every single time a change is done,
        //because without is only changes some times (not always)
        chessViewModel.pieceMovedTo.observeForever(observeChessMove)

        chessViewModel.player.observeForever(observePlayer)

        //Cycles board after observers is set
        chessViewModel.cycleBoard()

        var move: Position = empty

        var kingDead = false

        boardView.onTileClickedListener = { _: Tile, row: Int, column: Int ->

            val chess = ChessHelper(row, column, boardView)

            if (chessBoard[chess.pos] !is Empty || move != empty) {

                if (move == empty) {
                    if (chess.tryShowPossibleMoves(chessViewModel.player.value!!, chessBoard)) {
                        move = chess.pos
                    }
                } else {
                    val pos: Position =
                        Position.toPosition(viewToModelPosX(column), viewToModelPosY(row))
                    val moves: ArrayList<Position>?
                    val movePos = chessBoard[move]!!
                    moves = movePos.generatePossibleMoves(move, chessBoard)

                    if (moves.isNotEmpty() && movePos.canMove(pos)) {
                        if (chessBoard[pos]!!.pieceType == Piece.KING)
                            kingDead = true

                        chessViewModel.nextMove(move, pos)
                        chessViewModel.updatePlayerArmy()
                    }

                    //finishing activity
                    if (kingDead) {
                        if (chessViewModel.player.value == Army.BLACK) {
                            setResult(WHITE_WON)
                            finish()
                        } else {
                            setResult(BLACK_WON)
                            finish()
                        }
                    }

                    setSelectedPos(moves, false)
                    move = empty
                }
            } else {
                chess.setSelectedPos(false)
                move = empty
            }
        }
    }

    private fun setSelectedPos(moves: ArrayList<Position>?, b: Boolean) {
        moves?.forEach {
            boardView.setSelected(it, b)
        }
    }
}