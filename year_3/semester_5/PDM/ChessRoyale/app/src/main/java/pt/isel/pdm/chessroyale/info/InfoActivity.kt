package pt.isel.pdm.chessroyale.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chessroyale.databinding.ActivityInfoBinding

/**
 * Display info about the devs and the App itself
 */
class InfoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Make textView URL clickable
        binding.api.movementMethod = LinkMovementMethod.getInstance()
    }
}