package pt.isel.pdm.chessroyale.chess.puzzle

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.model.ChessHelper
import pt.isel.pdm.chessroyale.chess.model.pieces.Empty
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.ChessMove
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.chess.model.utils.Position.empty
import pt.isel.pdm.chessroyale.chess.view.Tile
import pt.isel.pdm.chessroyale.databinding.ActivityPuzzleBinding

/**
 * Activity used to solve the puzzle according to the solution provided
 */
class PuzzleActivity : AppCompatActivity() {

    private val puzzleViewModel: PuzzleActivityViewModel by viewModels()

    private val currPlayer: ImageView by lazy {
        findViewById(R.id.currPlayer)
    }

    private val binding by lazy {
        ActivityPuzzleBinding.inflate(layoutInflater)
    }

    private val chessBoard by lazy {
        puzzleViewModel.chessBoard!!
    }

    private val boardView by lazy {
        binding.boardView
    }

    private val boardModel by lazy {
        puzzleViewModel.chessBoardModel.value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //sends the pgn string to the view model for conversion and processing
        puzzleViewModel.processChessMoves(intent.getStringExtra("pgn").toString())
        puzzleViewModel.setPuzzleSolution(intent.getStringArrayExtra("solution")!!)

        //Creates an observer to notify the view of the board's changes
        val observeChessMove: Observer<ChessMove> = Observer<ChessMove> {
            boardView.setTile(it.from, boardModel.emptyPair())

            boardView.setTile(it.to, Pair(chessBoard[it.to]!!.army, chessBoard[it.to]!!.pieceType))
        }

        boardView.rotateBoard(puzzleViewModel.player.value!!)

        val observePlayer: Observer<Army> = Observer<Army> {
            if (puzzleViewModel.player.value == Army.BLACK)
                currPlayer.setImageResource(R.drawable.ic_black_king)
            else
                currPlayer.setImageResource(R.drawable.ic_white_king)
        }

        //Makes the previous observer notify the view every single time a change is done,
        //because without is only changes some times (not always)
        puzzleViewModel.pieceMovedTo.observeForever(observeChessMove)

        puzzleViewModel.player.observeForever(observePlayer)

        //Cycles board after observers is set
        puzzleViewModel.cycleBoard()

        var move: Position = empty

        boardView.onTileClickedListener = { _: Tile, row: Int, column: Int ->

            val solution: String =
                if (puzzleViewModel.puzzleSolution.value!!.isNotEmpty()) puzzleViewModel.puzzleSolution.value!![0] else ""
            val chess = ChessHelper(row, column, boardView)

            if (chessBoard[chess.pos] !is Empty || move != empty) {

                if (move == empty) {
                    if (chess.tryShowPossibleMoves(puzzleViewModel.player.value!!, chessBoard)) {
                        move = chess.pos
                    }
                } else if (solution.length == 4 && (move.name != solution.substring(
                        0,
                        2
                    ) || chess.pos.name != solution.substring(2, 4))
                ) {
                    if (!move.equalPos(chess.pos))
                        Toast.makeText(
                            applicationContext,
                            resources.getText(R.string.wrong_puzzle_move).toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    chess.moves = chessBoard[move]!!.generatePossibleMoves(move, chessBoard)

                    move = empty
                    chess.setSelectedPos(false)
                } else {
                    chess.move(
                        move,
                        chessBoard,
                        puzzleViewModel::nextMove,
                        puzzleViewModel::updatePlayerArmy
                    )

                    move = empty

                    puzzleViewModel.puzzleSolution.value!!.removeAt(0)

                    //finishing activity
                    if (puzzleViewModel.puzzleSolution.value!!.isEmpty()) {
                        setResult(RESULT_OK)
                        finish()
                    }

                    if (puzzleViewModel.puzzleSolution.value!!.isNotEmpty()) {
                        puzzleViewModel.nextMove(
                            Position.valueOf(
                                puzzleViewModel.puzzleSolution.value!![0]
                                    .substring(0, 2)
                            ),
                            Position.valueOf(
                                puzzleViewModel.puzzleSolution.value!![0]
                                    .substring(2, 4)
                            )
                        )
                        puzzleViewModel.updatePlayerArmy()
                        puzzleViewModel.puzzleSolution.value!!.removeAt(0)
                    }
                }
            } else {
                chess.setSelectedPos(false)
                move = empty
            }
        }
    }
}