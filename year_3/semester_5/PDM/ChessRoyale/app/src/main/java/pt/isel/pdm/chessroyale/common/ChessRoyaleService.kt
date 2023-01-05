package pt.isel.pdm.chessroyale.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

const val URL = "https://lichess.org/api/"

//region DTOs
@Parcelize
data class PuzzleInfoDTO(val game: GameDTO, val puzzle: PuzzleDTO) : Parcelable

@Parcelize
data class GameDTO(val pgn: String) : Parcelable

@Parcelize
data class PuzzleDTO(val id: String, val solution: Array<String>) : Parcelable
//endregion

//region API
@Parcelize
data class PuzzleInfo(
    val puzzleId: String,
    val date: Date,
    val puzzle: Puzzle,
    val solved: Boolean
) : Parcelable

@Parcelize
data class Puzzle(val pgn: String, val solution: Array<String>) : Parcelable
//endregion

/**
 * The abstraction that represents accesses to the remote API's resources.
 */
interface DailyPuzzleService {
    @GET("puzzle/daily")
    fun getDailyPuzzle(): Call<PuzzleInfoDTO>
}

/**
 * Represents errors while accessing the remote API. Instead of tossing around Retrofit errors,
 * we can use this exception to wrap them up.
 */
class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)
