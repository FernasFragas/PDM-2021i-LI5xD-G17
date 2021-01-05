package pt.isel.pdm.drag.list_games_activity

import android.app.Application
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.parcel.Parcelize
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.utils.ChallengeInfo
import pt.isel.pdm.drag.utils.Result
import pt.isel.pdm.drag.utils.State
import pt.isel.pdm.drag.utils.data.DragApplication
import java.lang.Exception


class ListGamesViewModel(app: Application) : AndroidViewModel(app) {

    /**
     * Contains information about the enrolment in a challenge.
     */
    val enrolmentResult: LiveData<Result<ChallengeInfo, Exception>> = MutableLiveData()

    /**
     * Contains the last fetched challenge list
     */
    val challenges: LiveData<List<ChallengeInfo>> = MutableLiveData()



    private val app = getApplication<DragApplication>()

    /**
     * Gets the challenges list by fetching them from the server. The operation's result is exposed
     * through [challenges]
     */
    fun fetchChallenges() {
        app.cloudRepository.fetchChallenges(
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
        app.cloudRepository.addPlayer(
                challengeInfo,
            { state.value = Result(State.COMPLETE, challengeInfo) },
            { error -> state.value = Result(State.COMPLETE, challengeInfo, error) }
        )
    }

    /**
     * Resets the state of the enrolment
     */
    fun resetEnrolmentResult() {
        (enrolmentResult as MutableLiveData<Result<ChallengeInfo, Exception>>).value = Result()
    }
}