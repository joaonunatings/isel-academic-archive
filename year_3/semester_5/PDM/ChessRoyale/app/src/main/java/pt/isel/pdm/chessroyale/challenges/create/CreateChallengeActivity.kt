package pt.isel.pdm.chessroyale.challenges.create

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import pt.isel.pdm.chessroyale.R
import pt.isel.pdm.chessroyale.chess.online.OnlineChessActivity
import pt.isel.pdm.chessroyale.chessroyale.MainActivity
import pt.isel.pdm.chessroyale.common.APP_TAG
import pt.isel.pdm.chessroyale.databinding.ActivityCreateChallengeBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The activity used to create a new challenge.
 */
class CreateChallengeActivity : AppCompatActivity() {

    private val viewModel: CreateChallengeViewModel by viewModels()
    private val binding: ActivityCreateChallengeBinding by lazy {
        ActivityCreateChallengeBinding.inflate(layoutInflater)
    }

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
     * Callback method that handles the activity initiation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.created.observe(this) {
            if (it == null) displayCreateChallenge()
            else it.onFailure { displayError() }.onSuccess {
                displayWaitingForChallenger()
            }
        }

        viewModel.accepted.observe(this) {
            if (it == true) {
                Log.v(APP_TAG, "Someone accepted our challenge")
                viewModel.created.value?.onSuccess { challengeInfo ->
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        Log.d(APP_TAG, "TEST") //TODO TEST
                        viewModel.removeChallenge()
                    }, 5, TimeUnit.SECONDS)

                    intentOnlineLauncher.launch(
                        OnlineChessActivity.buildIntent(
                            this,
                            challengeInfo.id,
                            0
                        )
                    )
                    viewModel.updateCreatedToNull()
                }
            }
        }

        binding.action.setOnClickListener {
            if (viewModel.created.value == null)
                viewModel.createChallenge(
                    binding.name.text.toString(),
                    binding.message.text.toString()
                )
            else viewModel.removeChallenge()
        }
    }

    /**
     * Displays the screen in its Create challenge state
     */
    private fun displayCreateChallenge() {
        binding.action.text = getString(R.string.create_challenge_button_label)
        with(binding.name) { text.clear(); isEnabled = true }
        with(binding.message) { text.clear(); isEnabled = true }
        binding.loading.isVisible = false
        binding.waitingMessage.isVisible = false
    }

    /**
     * Displays the screen in its Waiting for challenge state
     */
    private fun displayWaitingForChallenger() {
        binding.action.text = getString(R.string.cancel_challenge_button_label)
        binding.name.isEnabled = false
        binding.message.isEnabled = false
        binding.loading.isVisible = true
        binding.waitingMessage.isVisible = true
    }

    /**
     * Displays the screen in its error creating challenge state
     */
    private fun displayError() {
        displayCreateChallenge()
        Toast
            .makeText(this, R.string.error_creating_challenge, Toast.LENGTH_LONG)
            .show()
    }
}
