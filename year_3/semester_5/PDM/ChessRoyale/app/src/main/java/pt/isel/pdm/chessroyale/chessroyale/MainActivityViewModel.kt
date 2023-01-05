package pt.isel.pdm.chessroyale.chessroyale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.isel.pdm.chessroyale.common.ChessRoyaleApplication
import pt.isel.pdm.chessroyale.common.DailyPuzzleRepo
import pt.isel.pdm.chessroyale.common.PuzzleInfoDTO

class MainActivityViewModel(application: Application) :
    AndroidViewModel(application) {

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    // Gets daily puzzle from API to save locally and gets locally
    fun getDailyPuzzle(callback: (Result<PuzzleInfoDTO>) -> Unit) {
        val app = getApplication<ChessRoyaleApplication>()
        val repo = DailyPuzzleRepo(app.chessRoyaleService, app.historyDB.getHistoryPuzzleDAO())
        repo.fetchDailyPuzzle { result ->
            result
                .onFailure {
                    _error.value = it
                }
            repo.getDailyPuzzle(callback)
        }
    }

    // Updates solved status on DB
    fun updateSolvedOnDB(id : String, solved : Boolean, callback : (Result<Unit>) -> Unit) {
        val app = getApplication<ChessRoyaleApplication>()
        val repo = DailyPuzzleRepo(app.chessRoyaleService, app.historyDB.getHistoryPuzzleDAO())
        repo.updateSolvedOnDB(id, solved, callback)
    }
}