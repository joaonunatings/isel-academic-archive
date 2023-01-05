package pt.isel.pdm.chessroyale.challenges.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.challenges.ChallengeInfo
import pt.isel.pdm.chessroyale.challenges.create.CreateChallengeActivity
import pt.isel.pdm.chessroyale.chess.online.OnlineChessActivity
import pt.isel.pdm.chessroyale.chessroyale.MainActivity
import pt.isel.pdm.chessroyale.databinding.ActivityChallengesListBinding
import pt.isel.pdm.chessroyale.history.HistoryActivity
import pt.isel.pdm.chessroyale.info.InfoActivity

/**
 * The activity used to display the list of existing challenges.
 */
class ChallengesListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChallengesListBinding.inflate(layoutInflater) }
    private val viewModel: ChallengesListViewModel by viewModels()

    private val intentOnlineLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                MainActivity.WHITE_WON ->
                    Toast.makeText(
                        applicationContext,
                        resources.getText(R.string.won_msg).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                MainActivity.BLACK_WON ->
                    Toast.makeText(
                        applicationContext,
                        resources.getText(R.string.lost_msg).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                MainActivity.TIE ->
                    Toast.makeText(
                        applicationContext,
                        resources.getText(R.string.draw_game).toString(),
                        Toast.LENGTH_LONG
                    ).show()
            }
        }

    /**
     * Sets up the screen behaviour
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.challengesList.setHasFixedSize(true)
        binding.challengesList.layoutManager = LinearLayoutManager(this)

        viewModel.challenges.observe(this) { result ->
            result.onSuccess {
                binding.challengesList.adapter = ChallengesListAdapter(it, ::challengeSelected)
                binding.refreshLayout.isRefreshing = false
            }
            result.onFailure {
                Toast.makeText(this, R.string.error_getting_list, Toast.LENGTH_LONG).show()
            }
        }

        binding.refreshLayout.setOnRefreshListener { updateChallengesList() }
        binding.createChallengeButton.setOnClickListener {
            startActivity(Intent(this, CreateChallengeActivity::class.java))
        }

        viewModel.enrolmentResult.observe(this) {
            it?.onSuccess { challengeId ->
                intentOnlineLauncher.launch(OnlineChessActivity.buildIntent(this, challengeId, 1))
                viewModel.clearEnrolmentResultLiveData()
            }
        }
    }

    //region Upper right corner menu
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

    /**
     * The screen is about to become visible: refresh its contents.
     */
    override fun onStart() {
        super.onStart()
        updateChallengesList()
    }

    /**
     * Called whenever the challenges list is to be fetched again.
     */
    private fun updateChallengesList() {
        binding.refreshLayout.isRefreshing = true
        viewModel.fetchChallenges()
    }

    /**
     * Called whenever a list element is selected. The player that accepts the challenge is the
     * first to make a move.
     *
     * @param challenge the selected challenge
     */
    private fun challengeSelected(challenge: ChallengeInfo) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.accept_challenge_dialog_title, challenge.challengerName))
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                viewModel.tryAcceptChallenge(challenge, onFailure = {
                    Toast.makeText(this, R.string.join_challege_fail, Toast.LENGTH_SHORT).show()
                })
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
            .show()
    }
}