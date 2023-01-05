package pt.isel.pdm.chessroyale.common

import android.util.Log
import pt.isel.pdm.chessroyale.history.HistoryPuzzleDAO
import pt.isel.pdm.chessroyale.history.PuzzleEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Extension function to conveniently convert Entity to DTO
fun PuzzleEntity.toPuzzleInfoDTO() = PuzzleInfoDTO(
    game = GameDTO(this.pgn),
    puzzle = PuzzleDTO(id = this.id, solution = this.solution)
)

/**
 * Repository for Daily Puzzles
 * It's role is the one described here: https://developer.android.com/jetpack/guide
 *
 * The repository operations include I/O (network and/or DB accesses) using Kotlin's way: suspending Functions
 */
class DailyPuzzleRepo(
    private val dailyPuzzleService: DailyPuzzleService,
    private val historyPuzzleDAO: HistoryPuzzleDAO
) {

    // Gets the daily puzzle from the local DB, if available.
    private fun asyncMaybeGetDailyPuzzleFromDB(callback: (Result<PuzzleEntity?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyPuzzleDAO.getLast(1).firstOrNull()
        }
    }

    // Gets the daily puzzle from the remote API.
    private fun asyncGetDailyPuzzleFromAPI(callback: (Result<PuzzleInfoDTO>) -> Unit) {
        dailyPuzzleService.getDailyPuzzle().enqueue(
            object : Callback<PuzzleInfoDTO> {
                override fun onResponse(call: Call<PuzzleInfoDTO>, response: Response<PuzzleInfoDTO>) {
                    val dailyPuzzle: PuzzleInfoDTO? = response.body()
                    val result =
                        if (dailyPuzzle != null && response.isSuccessful)
                            Result.success(dailyPuzzle)
                        else
                            Result.failure(ServiceUnavailable())
                    callback(result)
                }

                override fun onFailure(call: Call<PuzzleInfoDTO>, error: Throwable) {
                    callback(Result.failure(ServiceUnavailable(cause = error)))
                }
            })
    }

    // Saves the daily puzzle to the local DB.
    private fun asyncSaveToDB(dto: PuzzleInfoDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyPuzzleDAO.insert(
                PuzzleEntity(
                    id = dto.puzzle.id,
                    pgn = dto.game.pgn,
                    solution = dto.puzzle.solution,
                    isSolved = false
                )
            )
        }
    }

    // Gets daily puzzle from local DB
    fun getDailyPuzzle(callback: (Result<PuzzleInfoDTO>) -> Unit) {
        asyncMaybeGetDailyPuzzleFromDB { maybeEntity ->
            val maybePuzzle = maybeEntity.getOrNull()
            if (maybePuzzle != null) {
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Got daily puzzle from local DB")
                callback(Result.success(maybePuzzle.toPuzzleInfoDTO()))
            } else {
                callback(Result.failure(ServiceUnavailable()))
            }
        }
    }

    // Fetches the daily puzzle from the remote API and saves it locally if not exists
    fun fetchDailyPuzzle(callback: (Result<Unit>) -> Unit) {
        asyncGetDailyPuzzleFromAPI { apiResult ->
            apiResult
                .onSuccess { PuzzleDto ->
                    Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Got daily puzzle from API")
                    asyncAlreadyExistsOnDB(PuzzleDto.puzzle.id) { alreadyExists ->
                        if (alreadyExists.getOrNull() == false) {
                            asyncSaveToDB(PuzzleDto) { saveToDBResult ->
                                saveToDBResult
                                    .onSuccess {
                                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Saved daily puzzle to local DB")
                                        callback(Result.success(Unit))
                                    }
                                    .onFailure {
                                        Log.e(APP_TAG, "Thread ${Thread.currentThread().name}: Failed to save daily puzzle to local DB", it)
                                        callback(Result.failure(it))
                                    }
                            }
                        } else {
                            callback(Result.success(Unit))
                        }
                    }
                }
                .onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    // Daily puzzle already exists
    fun asyncAlreadyExistsOnDB(id : String, callback: (Result<Boolean>) -> Unit) {
        callbackAfterAsync(callback) {
            historyPuzzleDAO.containsPrimaryKey(id)
        }
    }

    // Updates to the solved puzzle status
    fun updateSolvedOnDB(id : String, solved : Boolean, callback: (Result<Unit>) -> Unit) {
        callbackAfterAsync(callback) {
            historyPuzzleDAO.updateSolved(id, solved)
        }
    }
}
