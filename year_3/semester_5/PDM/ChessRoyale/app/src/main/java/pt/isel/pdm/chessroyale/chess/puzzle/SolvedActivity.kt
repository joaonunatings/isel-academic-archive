package pt.isel.pdm.chessroyale.chess.puzzle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.model.ChessHelper
import pt.isel.pdm.chessroyale.chess.model.utils.Army
import pt.isel.pdm.chessroyale.chess.model.utils.ChessMove
import pt.isel.pdm.chessroyale.chess.model.utils.Position
import pt.isel.pdm.chessroyale.databinding.ActivitySolvedPuzzleBinding

/**
 * The activity which displays the puzzle, either solved or not solved
 */
class SolvedActivity : AppCompatActivity() {

    // Uses the same ViewModel as PuzzleActivity since those activities require the same "tools".
    private val puzzleViewModel: PuzzleActivityViewModel by viewModels()

    private val binding by lazy {
        ActivitySolvedPuzzleBinding.inflate(layoutInflater)
    }

    private val currPlayer: ImageView by lazy {
        findViewById(R.id.currPlayer)
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

    private val intentPuzzleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val toast = Toast.makeText(
                    applicationContext,
                    resources.getText(R.string.puzzle_won).toString(),
                    Toast.LENGTH_LONG
                )
                toast.show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Sends the pgn string to the view model for conversion and processing
        puzzleViewModel.processChessMoves(intent.getStringExtra("pgn").toString())
        puzzleViewModel.setPuzzleSolution(intent.getStringArrayExtra("solution")!!)

        val observePlayer: Observer<Army> = Observer<Army> {
            if (puzzleViewModel.player.value == Army.BLACK)
                currPlayer.setImageResource(R.drawable.ic_black_king)
            else
                currPlayer.setImageResource(R.drawable.ic_white_king)
        }

        puzzleViewModel.player.observeForever(observePlayer)

        //Creates an observer to notify the view of the board's changes
        val observeChessMove: Observer<ChessMove> = Observer<ChessMove> {
            boardView.setTile(it.from, boardModel.emptyPair())

            boardView.setTile(it.to, Pair(chessBoard[it.to]!!.army, chessBoard[it.to]!!.pieceType))
        }

        //Makes the previous observer notify the view every single time a change is done,
        //because without is only changes some times (not always)
        puzzleViewModel.pieceMovedTo.observeForever(observeChessMove)

        //Cycles board after observers is set
        puzzleViewModel.cycleBoard()

        binding.replayButton.setOnClickListener {
            val puzzleIntent = Intent(this, PuzzleActivity::class.java)
            puzzleIntent.putExtra("pgn", intent.getStringExtra("pgn").toString())
                .putExtra("solution", intent.getStringArrayExtra("solution")!!)
            intentPuzzleLauncher.launch(puzzleIntent)
        }

        binding.solutionButton.setOnClickListener {
            binding.solvedTitle.setText(R.string.solved_puzzle_title)
            puzzleViewModel.solvedButton.value = false
            solvePuzzle()
        }

        val observeSolvedButton: Observer<Boolean> = Observer<Boolean> {
            binding.solutionButton.isEnabled = it
        }

        puzzleViewModel.solvedButton.observeForever(observeSolvedButton)
    }

    private fun solvePuzzle() {
        puzzleViewModel.puzzleSolution.value!!.forEach { move ->
            val moveFrom: Position = Position.valueOf(move.substring(0, 2))
            val chess = ChessHelper(
                7.minus(move[3].digitToInt()).plus(1),
                move[2].code.minus('a'.code),
                boardView
            )
            chess.move(
                moveFrom,
                chessBoard,
                puzzleViewModel::nextMove,
                puzzleViewModel::updatePlayerArmy
            )
        }
    }
}