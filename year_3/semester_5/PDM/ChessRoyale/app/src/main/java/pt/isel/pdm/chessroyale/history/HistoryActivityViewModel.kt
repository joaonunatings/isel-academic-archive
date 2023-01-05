package pt.isel.pdm.chessroyale.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import pt.isel.pdm.chessroyale.common.ChessRoyaleApplication
import pt.isel.pdm.chessroyale.common.DailyPuzzleRepo
import pt.isel.pdm.chessroyale.common.PuzzleInfo
import pt.isel.pdm.chessroyale.common.callbackAfterAsync

class HistoryActivityViewModel(application: Application) : AndroidViewModel(application) {

    var history: LiveData<List<PuzzleInfo>>? = null
        private set

    private val puzzleDao: HistoryPuzzleDAO by lazy {
        getApplication<ChessRoyaleApplication>().historyDB.getHistoryPuzzleDAO()
    }

    // Holds a LiveData with the list of puzzles
    fun loadHistory(): LiveData<List<PuzzleInfo>> {
        val publish = MutableLiveData<List<PuzzleInfo>>()
        history = publish
        callbackAfterAsync(
            asyncAction = {
                puzzleDao.getAll().map { // PuzzleEntity to PuzzleInfo
                    it.toPuzzleInfo()
                }
            },
            callback = { result ->
                result.onSuccess { publish.value = it }
                result.onFailure { publish.value = emptyList() }
            }
        )

        return publish
    }

    // Updates the puzzle as solved in the DB
    fun updateSolvedOnDB(id : String, solved : Boolean, callback : (Result<Unit>) -> Unit) {
        val repo = DailyPuzzleRepo(getApplication<ChessRoyaleApplication>().chessRoyaleService, puzzleDao)
        repo.updateSolvedOnDB(id, solved, callback)
    }
}