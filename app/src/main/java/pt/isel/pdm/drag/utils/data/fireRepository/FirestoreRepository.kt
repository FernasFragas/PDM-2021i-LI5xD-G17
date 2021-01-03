package pt.isel.pdm.drag.utils.data.fireRepository

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.isel.pdm.drag.utils.ChallengeInfo


/**
 * The path of the Firestore collection that contains all the challenges
 */
private const val CHALLENGES_COLLECTION = "challenges_inFundraising"

private const val CHALLENGE_NAME = "Challenge Name"
private const val CHALLENGE_MESSAGE = "Number Of Players Needed"


/**
 * Extension function used to convert createdChallenge documents stored in the Firestore DB into
 * [ChallengeInfo] instances
 */
private fun QueryDocumentSnapshot.toChallengeInfo() =
        ChallengeInfo(
                id,
                data[CHALLENGE_NAME] as String,
                data[CHALLENGE_MESSAGE] as Int
        )



/**
 * the repo for the distributed game mode
 */
class FirestoreRepository(private val mapper: ObjectMapper) {


    /**
     * Publishes a challenge with the given [gameName] and [playersNumb]
     *
     * Create in the firestore a new "package" in the
     * /challenges_inFundraising package with a random id that
     * identifies the new challenge, this new package will have 2 fields
     * [gameName] will be added in Challenge Name
     * [playersNumb] will be added in Number Of Players Needed
     */
    fun publishChallenge(gameName: String, playersNumb: Int,
                         onSuccess: (ChallengeInfo) -> Unit, onError: (Exception) -> Unit) {

        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .add(hashMapOf(CHALLENGE_NAME to gameName, CHALLENGE_MESSAGE to playersNumb))
                .addOnSuccessListener{onSuccess(ChallengeInfo(it.id, gameName, playersNumb))}
                .addOnFailureListener{onError(it)}
    }


    /**
     * Unpublishes the challenge with the given identifier.
     *
     * When the challenge have enough players to be played this method removes the game from the
     * package /challenges_inFundraising
     */
    fun unPublishChallenge(challengeId: String,
                           onSuccess: () -> Unit, onError: (Exception) -> Unit) {

        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .document(challengeId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
    }

    /**
     * Fetches the list of open challenges from the backend
     */
    fun fetchOnGoingChallenges(onSuccess: (List<ChallengeInfo>) -> Unit,
                               onError: (Exception) -> Unit) {

        FirebaseFirestore.getInstance()
                .collection(CHALLENGES_COLLECTION)
                .get()  // in realistic scenarios is not the best design because the size can be unbounded
                .addOnSuccessListener { result ->
                    onSuccess(result.map { it.toChallengeInfo() }.toList())
                }
                .addOnFailureListener{ onError(it) }
    }


}