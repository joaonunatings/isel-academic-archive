package pt.isel.pdm.chessroyale.chessroyale

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import pt.isel.pdm.chessroyale.common.APP_TAG
import pt.isel.pdm.chessroyale.common.ChessRoyaleApplication
import pt.isel.pdm.chessroyale.common.DailyPuzzleRepo

/**
 * Definition of the background job that fetches the daily puzzles and stores it in the history DB,
 * using CoroutineWorkers
 */
class DownloadDailyPuzzleWorker(appContext: Context, workerParams: WorkerParameters)
    : ListenableWorker(appContext, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val app : ChessRoyaleApplication = applicationContext as ChessRoyaleApplication
        val repo = DailyPuzzleRepo(app.chessRoyaleService, app.historyDB.getHistoryPuzzleDAO())

        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Starting DownloadDailyPuzzleWorker")

        return CallbackToFutureAdapter.getFuture { completer ->
            repo.fetchDailyPuzzle { result ->
                result
                    .onSuccess {
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: DownloadDailyPuzzleWorker succeeded")
                        completer.set(Result.success())
                    }
                    .onFailure {
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: DownloadDailyPuzzleWorker failed")
                        completer.setException(it)
                    }
            }
        }
    }
}