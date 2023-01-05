package pt.isel.pdm.chessroyale.history

import androidx.room.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

const val PUZZLE_TABLE_NAME = "history_puzzle"

/**
 * The data type that represents data stored in the "history_puzzle" table of the DB
 */
@Entity(tableName = PUZZLE_TABLE_NAME)
data class PuzzleEntity(
    @PrimaryKey val id: String,
    val pgn: String,
    val solution: Array<String>,
    val isSolved: Boolean,
    val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
)

/**
 * Contains converters used by the ROOM ORM to map between Kotlin types and MySQL types
 */
class Converters {
    @TypeConverter
    fun stringToArray(value: String): Array<String> = value.split(" ").toTypedArray()

    @TypeConverter
    fun arrayToString(value: Array<String>): String = value.joinToString(" ")

    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
}

/**
 * The abstraction containing the supported data access operations. The actual implementation is
 * provided by the Room compiler.
 */
@Dao
interface HistoryPuzzleDAO {
    @Insert
    fun insert(puzzle: PuzzleEntity)

    @Delete
    fun delete(puzzle: PuzzleEntity)

    @Query("SELECT * FROM $PUZZLE_TABLE_NAME ORDER BY timestamp DESC LIMIT 100")
    fun getAll(): List<PuzzleEntity>

    @Query("SELECT * FROM $PUZZLE_TABLE_NAME ORDER BY timestamp DESC LIMIT :count")
    fun getLast(count: Int): List<PuzzleEntity>

    @Query("SELECT count(*)!=0 FROM $PUZZLE_TABLE_NAME WHERE id = :id")
    fun containsPrimaryKey(id: String): Boolean

    @Query("UPDATE $PUZZLE_TABLE_NAME SET isSolved = :solved WHERE id = :id")
    fun updateSolved(id: String, solved: Boolean)
}

/**
 * The abstraction that represents the DB itself. It is also used as a DAO factory: one factory
 * method per DAO.
 */
@Database(entities = [PuzzleEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun getHistoryPuzzleDAO(): HistoryPuzzleDAO
}