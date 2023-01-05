package pt.isel.pdm.chessroyale.common

import android.app.Application
import androidx.room.Room
import androidx.work.*
import pt.isel.pdm.chessroyale.challenges.ChallengesRepository
import pt.isel.pdm.chessroyale.chess.online.GamesRepository
import pt.isel.pdm.chessroyale.chessroyale.DownloadDailyPuzzleWorker
import pt.isel.pdm.chessroyale.history.HistoryDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val APP_TAG = "CHESS_ROYALE_TAG"

/**
 * Global Application Object that allows context to all the different modules, giving access to all
 * resources and "global methods"
 */
class ChessRoyaleApplication : Application() {

    val chessRoyaleService: DailyPuzzleService by lazy {
        Retrofit
            .Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DailyPuzzleService::class.java)
    }

    /**
     * Object that contains the database
     */
    val historyDB: HistoryDatabase by lazy {
        Room
            .databaseBuilder(this, HistoryDatabase::class.java, "history_db")
            .build()
    }

    /**
     * The challenges' repository
     */
    val challengesRepository: ChallengesRepository by lazy { ChallengesRepository() }

    /**
     * The games' repository
     */
    val gamesRepository: GamesRepository by lazy { GamesRepository() }

    /**
     * Called each time the application process is loaded
     */
    override fun onCreate() {
        super.onCreate()
        val workRequest = PeriodicWorkRequestBuilder<DownloadDailyPuzzleWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "DownloadDailyPuzzle",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}