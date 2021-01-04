package pt.isel.pdm.drag.utils.data.fireRepository

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.isel.pdm.drag.utils.ChallengeInfo


/**
 * The path of the Firestore collection that contains all the challenges
 */
private const val CHALLENGES_COLLECTION = "challenges"

private const val CHALLENGE_NAME = "Challenge Name"
private const val PLAYER_CAPACITY = "Player Capacity"
private const val PLAYER_COUNT = "Number of players in challenge"
private const val ROUND_NUMBER = "Number of rounds"


/**
 * Extension function used to convert createdChallenge documents stored in the Firestore DB into
 * [ChallengeInfo] instances
 */
private fun QueryDocumentSnapshot.toChallengeInfo() =
        ChallengeInfo(
                id,
                data[CHALLENGE_NAME] as String,
            /*
                5,
                2,
                3
             */
                data[PLAYER_CAPACITY] as Long,
                data[PLAYER_COUNT] as Long,
                data[ROUND_NUMBER] as Long

        )



/**
 * the repo for the distributed game mode
 */
class FirestoreRepository(private val mapper: ObjectMapper) {


    /**
     * Publishes a challenge with the given [gameName] and [playersCapacity]
     *
     * Create in the firestore a new "package" in the
     * /challenges_inFundraising package with a random id that
     * identifies the new challenge, this new package will have 2 fields
     * [gameName] will be added in Challenge Name
     * [playersCapacity] will be added in Number Of Players Needed
     */
    fun publishChallenge(
            gameName: String,
            playersCapacity: Int,
            roundNumber: Int,
            onSuccess: (ChallengeInfo) -> Unit,
            onError: (Exception) -> Unit) {

        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .add(hashMapOf(CHALLENGE_NAME to gameName, PLAYER_CAPACITY to playersCapacity,
                        PLAYER_CAPACITY to playersCapacity.toLong(), PLAYER_COUNT to 1,
                        ROUND_NUMBER to roundNumber))
                .addOnSuccessListener{
                    onSuccess(
                            ChallengeInfo(
                                    it.id,
                                    gameName,
                                    playersCapacity.toLong(),
                                    1,
                                    roundNumber.toLong())
                    )
                }
                .addOnFailureListener{onError(it)}
    }


    /**
     * Unpublishes the challenge with the given identifier.
     *
     * When the challenge have enough players to be played this method removes the game from the
     * package /challenges_inFundraising
     */
    private fun unPublishChallenge(challengeId: String,
                           onSuccess: () -> Unit, onError: (Exception) -> Unit) {

        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .document(challengeId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
    }

    /**
    * updates the challenge info in the cloud with the new number of players that already enroll the
    * challenge
    */
    private fun updatePlayerCount(challengeInfo: ChallengeInfo,
                                  onSuccess: () -> Unit,
                                  onError: (Exception) -> Unit) {
        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .document(challengeInfo.id)
                .update(PLAYER_COUNT, challengeInfo.playerNum)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }

    }

    /**
     * updates the challenge with a new player and,
     *
     * if the number of players is completed removes the challenge from
     * the list of the the games in fundraising state
     *
     * if the number of players with the new player is not enough to start the game, only updates
     * the challenge info in the cloud with the new number of players that already enroll the
     * challenge
     */
    fun addPlayer(challengeInfo: ChallengeInfo,
                  onSuccess: () -> Unit,
                  onError: (Exception) -> Unit) {
        challengeInfo.playerNum = challengeInfo.playerNum + 1
        if (challengeInfo.playerNum == challengeInfo.playerCapacity)
            unPublishChallenge(challengeInfo.id, onSuccess, onError)
        else
            updatePlayerCount(challengeInfo, onSuccess, onError)
    }

    /**
     * Fetches the list of open challenges from the backend
     */
    fun fetchChallenges(onSuccess: (List<ChallengeInfo>) -> Unit,
                        onError: (Exception) -> Unit) {

        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .get()  // in realistic scenarios is not the best design because the size can be unbounded
                .addOnSuccessListener { result ->
                    Log.v("TESTE123", "Repo got list from Firestore")
                    onSuccess(result.map { it.toChallengeInfo() }.toList())
                }
                .addOnFailureListener{ onError(it) }
    }


}