package pt.isel.pdm.drag.list_games_activity

import android.app.Application
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.parcel.Parcelize
import java.lang.Exception


/**
 * The challenge information.
 *
 * @property [id]                   the challenge identifier
 * @property [challengerName]       the challenger name
 * @property [challengerMessage]    the challenger message
 */
@Parcelize
data class ChallengeInfo(
    val id: String,
    val challengerName: String,
    val challengerMessage: String
) : Parcelable

enum class State { IDLE, ONGOING, COMPLETE }

data class Result<R, E>(
    val state: State = State.IDLE,
    val result: R? = null,
    val error: E? = null
)

class ListGamesViewModel(app: Application) : AndroidViewModel(app) {

    /**
     * Contains information about the enrolment in a challenge.
     */
    val enrolmentResult: LiveData<Result<ChallengeInfo, Exception>> = MutableLiveData()

    /**
     * Contains the last fetched challenge list
     */
    val challenges: LiveData<List<ChallengeInfo>> = MutableLiveData()


    /*
    private val app = getApplication<TicTacToeApplication>()

    /**
     * Gets the challenges list by fetching them from the server. The operation's result is exposed
     * through [challenges]
     */
    fun fetchChallenges() {
        app.repository.fetchChallenges(
            onSuccess = {
                (challenges as MutableLiveData<List<ChallengeInfo>>).value = it
            },
            onError = {
                Toast.makeText(app, R.string.error_getting_list, Toast.LENGTH_LONG).show()
            }
        )
    }

    /**
     * Tries to accepts the given challenge. The result of the asynchronous operation is exposed
     * through [enrolmentResult]
     */
    fun tryAcceptChallenge(challengeInfo: ChallengeInfo) {
        val state = enrolmentResult as MutableLiveData<Result<ChallengeInfo, Exception>>
        state.value = Result(State.ONGOING, challengeInfo)
        app.repository.unpublishChallenge(
            challengeInfo.id,
            { state.value = Result(State.COMPLETE, challengeInfo) },
            { error -> state.value = Result(State.COMPLETE, challengeInfo, error) }
        )
    }
     */

    /**
     * Resets the state of the enrolment
     */
    fun resetEnrolmentResult() {
        (enrolmentResult as MutableLiveData<Result<ChallengeInfo, Exception>>).value = Result()
    }
}