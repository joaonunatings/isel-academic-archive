package pt.isel.pdm.chessroyale.chess.online

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.model.ChessHelper
import pt.isel.pdm.chessroyale.chess.model.pieces.Empty
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.ChessMove
import pt.isel.pdm.chessroyale.chess.model.utils.Piece
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.chess.view.Tile
import pt.isel.pdm.chessroyale.chessroyale.MainActivity.Companion.BLACK_WON
import pt.isel.pdm.chessroyale.chessroyale.MainActivity.Companion.TIE
import pt.isel.pdm.chessroyale.chessroyale.MainActivity.Companion.WHITE_WON
import pt.isel.pdm.chessroyale.common.APP_TAG
import pt.isel.pdm.chessroyale.databinding.ActivityOnlineChessBinding

/**
 * Online chess activity that allows two players to match each other on different devices
 */
class OnlineChessActivity : AppCompatActivity() {

    companion object {
        fun buildIntent(origin: Context, challengeId: String, player: Int) =
            Intent(origin, OnlineChessActivity::class.java)
                .putExtra(ONLINE_CHESS_ACTIVITY_CHALLENGE, challengeId)
                .putExtra(ONLINE_CHESS_ACTIVITY_ONLINE_PLAYER, player)
    }

    private val binding: ActivityOnlineChessBinding by lazy {
        ActivityOnlineChessBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel: OnlineChessActivityViewModel by viewModels()

    private val currPlayer: ImageView by lazy {
        findViewById(R.id.currPlayer)
    }

    private val chessBoard by lazy {
        viewModel.chessBoard!!
    }

    private val boardView by lazy {
        binding.boardView
    }

    private val boardModel by lazy {
        viewModel.chessBoardModel.value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.setOnlinePlayer(intent.getIntExtra(ONLINE_CHESS_ACTIVITY_ONLINE_PLAYER, 2))
        viewModel.setChallengeId(intent.getStringExtra(ONLINE_CHESS_ACTIVITY_CHALLENGE))

        // If player uses black pieces the board inverts so that their pieces are on his side of the phone
        if (viewModel.getPlayer() == Army.BLACK) {
            boardView.rotateBoard(Army.BLACK)
        }

        // Creates an observer to notify the view of the board's changes
        val observeChessMove: Observer<ChessMove> = Observer<ChessMove> {
            boardView.setTile(it.from, boardModel.emptyPair())
            boardView.setTile(it.to, Pair(chessBoard[it.to]!!.army, chessBoard[it.to]!!.pieceType))
        }

        // Updates current player image
        val observePlayer: Observer<Army> = Observer<Army> {
            if (viewModel.player.value == Army.BLACK)
                currPlayer.setImageResource(R.drawable.ic_black_king)
            else
                currPlayer.setImageResource(R.drawable.ic_white_king)
        }

        //
        val observeLastMove: Observer<String> = Observer<String> {
            if (viewModel.player.value != viewModel.getPlayer() && chessBoard[Position.toPosition(
                    viewModel.getLastMove()[0].code.minus('a'.code).plus(1),
                    viewModel.getLastMove()[1].digitToInt()
                )]!!.pieceType != Piece.EMPTY
            ) {
                solvePuzzle(viewModel.getLastMove())
            }
        }

        val observeDraw: Observer<Boolean?> = Observer<Boolean?> {
            when (it) {
                null -> {
                    if (binding.drawSwitch.isChecked) {
                        endGameWithResult(TIE)
                    } else {
                        endGameWithResult(WHITE_WON)
                    }
                }
                false -> {
                    if (viewModel.localDraw.value == true) {
                        binding.drawSwitch.isChecked = false
                        viewModel.localDraw.value = false
                        Toast.makeText(this, R.string.draw_rejected, Toast.LENGTH_SHORT).show()
                    } else if (viewModel.localDraw.value == false) {
                        showDraw(false)
                    }
                }
                true -> {
                    if (viewModel.localDraw.value == false && binding.drawText.isInvisible) {
                        Toast.makeText(this, R.string.draw_offer, Toast.LENGTH_SHORT).show()
                        showDraw(true)
                    }
                }
            }
        }

        viewModel.draw.observe(this, observeDraw)

        viewModel.lastMove.observeForever(observeLastMove)

        //Makes the previous observer notify the view every single time a change is done,
        //because without is only changes some times (not always)
        viewModel.pieceMovedTo.observeForever(observeChessMove)

        viewModel.player.observeForever(observePlayer)

        //Cycles board after observers is set
        viewModel.cycleBoard()

        onBackPressedDispatcher.addCallback {
            giveUpAction()
        }

        binding.forfeitButton.setOnClickListener {
            giveUpAction()
        }

        binding.drawSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && viewModel.draw.value == null) {
                viewModel.updateDraw(null)
                endGameWithResult(TIE)
            } else if (isChecked) {
                viewModel.localDraw.value = true
                viewModel.updateDraw(true)
            } else {
                viewModel.localDraw.value = false
                viewModel.updateDraw(false)
            }
        }

        binding.acceptButton.setOnClickListener {
            viewModel.updateDraw(null)
            endGameWithResult(TIE)
        }

        binding.declineButton.setOnClickListener {
            viewModel.updateDraw(false)
            showDraw(false)
        }
        var move: Position = Position.empty

        var kingDead = false

        boardView.onTileClickedListener = { _: Tile, row: Int, column: Int ->

            if (viewModel.player.value == viewModel.getPlayer()) {
                val chess = ChessHelper(row, column, boardView)

                if (chessBoard[chess.pos] !is Empty || move != Position.empty) {

                    if (move == Position.empty) {
                        if (chess.tryShowPossibleMoves(
                                viewModel.player.value!!,
                                chessBoard
                            )
                        ) {
                            move = chess.pos
                        }
                    } else {
                        val pos: Position = Position.toPosition(
                            ChessHelper.viewToModelPosX(column),
                            ChessHelper.viewToModelPosY(row)
                        )
                        val moves: ArrayList<Position>?
                        val movePos = chessBoard[move]!!
                        moves = movePos.generatePossibleMoves(move, chessBoard)

                        if (moves.isNotEmpty() && movePos.canMove(pos)) {
                            if (chessBoard[pos]!!.pieceType == Piece.KING)
                                kingDead = true

                            viewModel.nextMove(move, pos)
                            viewModel.updatePlayerArmy()
                            viewModel.updateMove()
                        }

                        //finishing activity
                        if (kingDead) {
                            gameOver()
                        }

                        setSelectedPos(moves, false)
                        move = Position.empty
                    }
                } else {
                    chess.setSelectedPos(false)
                    move = Position.empty
                }
            }
        }
    }

    private fun gameOver() {
        if (viewModel.getPlayer() != viewModel.player.value) {
            endGameWithResult(WHITE_WON)
        } else {
            endGameWithResult(BLACK_WON)
        }
    }

    private fun endGameWithResult(result: Int) {
        viewModel.gameSubscription.remove()
        setResult(result)
        finish()
    }

    private fun setSelectedPos(moves: ArrayList<Position>?, b: Boolean) {
        moves?.forEach {
            boardView.setSelected(it, b)
        }
    }

    private fun solvePuzzle(move: String) {
        val moveFrom: Position =
            Position.valueOf(move.substring(0, 2))
        val chess = ChessHelper(
            7.minus(move[3].digitToInt()).plus(1),
            move[2].code.minus('a'.code),
            boardView
        )

        val isKingDead = chessBoard[chess.pos]!!.pieceType == Piece.KING

        if (chess.move(
                moveFrom,
                chessBoard,
                viewModel::nextMove,
                viewModel::updatePlayerArmy
            )
        ) {

            if (isKingDead) {
                gameOver()
                viewModel.removeGame()
            }
        }
    }

    private fun showDraw(show: Boolean) {
        binding.drawText.isInvisible = !show
        binding.acceptButton.isInvisible = !show
        binding.declineButton.isInvisible = !show
        binding.drawSwitch.isInvisible = show
    }

    private fun giveUpAction() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.forfeit_dialog_title))
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                endGameWithResult(BLACK_WON)
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
            .show()
    }
}