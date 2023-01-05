package pt.isel.pdm.chessroyale.chessroyale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.challenges.list.ChallengesListActivity
import pt.isel.pdm.chessroyale.chess.offline.ChessActivity
import pt.isel.pdm.chessroyale.chess.puzzle.PuzzleActivity
import pt.isel.pdm.chessroyale.databinding.ActivityMainBinding
import pt.isel.pdm.chessroyale.history.HistoryActivity
import pt.isel.pdm.chessroyale.info.InfoActivity

/**
 * The main application screen, displaying all the app functionalities
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val WHITE_WON = 5
        const val BLACK_WON = -5
        const val TIE = 10
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: MainActivityViewModel by viewModels()
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
                }
            }
        }

    private val intentLocalLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == WHITE_WON) {
                Toast.makeText(
                    applicationContext,
                    resources.getText(R.string.white_won).toString(),
                    Toast.LENGTH_LONG
                ).show()
            } else if (it.resultCode == BLACK_WON) {
                Toast.makeText(
                    applicationContext,
                    resources.getText(R.string.black_won).toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.puzzleButton.setOnClickListener {
            binding.puzzleButton.isEnabled = false
            viewModel.getDailyPuzzle { result ->
                result
                    .onSuccess { puzzleInfo ->
                        val puzzleIntent = Intent(this, PuzzleActivity::class.java)
                        puzzleIntent.putExtra("pgn", puzzleInfo.game.pgn)
                            .putExtra("solution", puzzleInfo.puzzle.solution)
                        intentPuzzleLauncher.launch(puzzleIntent)
                        puzzleId = puzzleInfo.puzzle.id
                    }
                    .onFailure {
                        displayNoPuzzleError()
                    }
                binding.puzzleButton.isEnabled = true
            }
        }

        binding.localButton.setOnClickListener {
            binding.localButton.isEnabled = false
            intentLocalLauncher.launch(Intent(this, ChessActivity::class.java))
            binding.localButton.isEnabled = true
        }

        binding.onlineButton.setOnClickListener {
            binding.localButton.isEnabled = false
            intentLocalLauncher.launch(Intent(this, ChallengesListActivity::class.java))
            binding.localButton.isEnabled = true
        }

        viewModel.error.observe(this) {
            displayError()
        }
    }

    // Renders the options on the top right end side, the InfoActivity and HistoryActivity
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                startActivity(Intent(this, InfoActivity::class.java))
                true
            }
            R.id.history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayError() {
        Toast.makeText(
            applicationContext,
            resources.getText(R.string.fetching_error).toString(),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun displayNoPuzzleError() {
        Toast.makeText(
            applicationContext,
            resources.getText(R.string.no_puzzle),
            Toast.LENGTH_SHORT
        ).show()
    }
}