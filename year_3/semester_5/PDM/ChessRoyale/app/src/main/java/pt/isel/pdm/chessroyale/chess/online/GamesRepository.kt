package pt.isel.pdm.chessroyale.chess.online

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isel.pdm.chessroyale.challenges.ChallengeInfo

/**
 * The path of the Firestore collection that contains all the active games
 */
private const val GAMES_COLLECTION = "games"

const val MOVE_STATE_KEY = "move"
const val PLAYER_STATE_KEY = "player"
const val DRAW_STATE_KEY = "draw"

/**
 * The repository for the ChessRoyale games, implemented using Firebase.
 */
class GamesRepository {

    /**
     * Creates the game for the given challenge ID , 3, false, false
     */
    fun createGame(
        challenge: ChallengeInfo,
        onComplete: (Result<String>) -> Unit
    ) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challenge.id)
            .set(GameData.builder())
            .addOnSuccessListener { onComplete(Result.success(challenge.id)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    /**
     * Updates the shared game state
     */
    fun updateMove(
        gameId: String,
        move: String,
        player: Boolean,
        onComplete: (Result<MoveData>) -> Unit
    ) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(gameId)
            .update(MoveData.builder(move, player))
            .addOnSuccessListener { onComplete(Result.success(MoveData(move, player))) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    /**
     * Updates the shared game state
     */
    fun updateDraw(gameId: String, draw: Boolean?, onComplete: (Result<Boolean?>) -> Unit) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(gameId)
            .update(DrawData.builder(draw))
            .addOnSuccessListener { onComplete(Result.success(draw)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }

    /**
     * Subscribes for changes in the challenge identified by [challengeId]
     */
    fun subscribeToGameStateChanges(
        challengeId: String,
        onSubscriptionError: (Exception) -> Unit,
        onMoveDataChange: (MoveData) -> Unit,
        onDrawDataChange: (DrawData) -> Unit,
        onDisconnect: (Boolean) -> Unit
    ): ListenerRegistration {

        return Firebase.firestore
            .collection(GAMES_COLLECTION)
            .document(challengeId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener
                }

                if (snapshots?.exists() == true) {
                    val drawData = DrawData(snapshots.get(DRAW_STATE_KEY) as Boolean?)
                    onDrawDataChange(drawData)

                    val moveData = MoveData(
                        snapshots.get(MOVE_STATE_KEY) as String, snapshots.get(
                            PLAYER_STATE_KEY
                        ) as Boolean
                    )
                    onMoveDataChange(moveData)
                } else {
                    onDisconnect(true)
                }
            }
    }

    /**
     * Deletes the shared game state for the given challenge.
     */
    fun deleteGame(challengeId: String, onComplete: (Result<Unit>) -> Unit) {
        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onComplete(Result.success(Unit)) }
            .addOnFailureListener { onComplete(Result.failure(it)) }
    }
}

