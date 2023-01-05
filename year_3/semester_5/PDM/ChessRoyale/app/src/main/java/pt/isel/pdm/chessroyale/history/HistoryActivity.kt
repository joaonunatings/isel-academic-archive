package pt.isel.pdm.chessroyale.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.puzzle.PuzzleActivity
import pt.isel.pdm.chessroyale.chess.puzzle.SolvedActivity
import pt.isel.pdm.chessroyale.common.APP_TAG
import pt.isel.pdm.chessroyale.common.Puzzle
import pt.isel.pdm.chessroyale.common.PuzzleInfo
import pt.isel.pdm.chessroyale.databinding.ActivityHistoryBinding

fun PuzzleEntity.toPuzzleInfo() = PuzzleInfo(
    puzzleId = this.id,
    date = this.timestamp,
    puzzle = Puzzle(this.pgn, this.solution),
    solved = this.isSolved
)

/**
 * Supports all previous puzzle games through a Recycler View, being able to view solution/solve them.
 */
class HistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HistoryActivityViewModel>()
    private var puzzleId = ""

    private val intentPuzzleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    applicationContext,
                    resources.getText(R.string.puzzle_won).toString(),
                    Toast.LENGTH_LONG
                ).show()

                viewModel.updateSolvedOnDB(puzzleId, true) { result ->
                    result.onFailure {
                        Toast.makeText(
                            applicationContext,
                            resources.getText(R.string.error).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    instantiateAdapter()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set list layout manager
        binding.puzzleList.layoutManager = LinearLayoutManager(this)

        instantiateAdapter()
    }

    // Instantiates a new adapter to be able to update list
    private fun instantiateAdapter() {
        viewModel.loadHistory().observeForever {
            binding.puzzleList.adapter = HistoryAdapter(it) { dailyPuzzle ->
                val puzzleIntent =
                    Intent(
                        this,
                        if (dailyPuzzle.solved) SolvedActivity::class.java else PuzzleActivity::class.java
                    )
                puzzleIntent.putExtra("pgn", dailyPuzzle.puzzle.pgn)
                    .putExtra("solution", dailyPuzzle.puzzle.solution)
                intentPuzzleLauncher.launch(puzzleIntent)
                puzzleId = dailyPuzzle.puzzleId
            }
        }
    }
}