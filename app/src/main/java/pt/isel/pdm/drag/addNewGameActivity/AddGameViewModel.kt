package pt.isel.pdm.drag.addNewGameActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.drag.startActivity.MAX_PLAYERS
import pt.isel.pdm.drag.startActivity.MAX_ROUNDS
import pt.isel.pdm.drag.startActivity.MIN_PLAYERS
import pt.isel.pdm.drag.startActivity.MIN_ROUNDS
import pt.isel.pdm.drag.utils.ChallengeInfo
import pt.isel.pdm.drag.utils.State
import pt.isel.pdm.drag.utils.data.DragApplication
import pt.isel.pdm.drag.utils.Result


/**
 * The View Model to be used in the [AddGameActivity].
 *
 * Challenges are created by participants and are posted on the server, awaiting acceptance.
 */
class AddGameViewModel(application: Application) : AndroidViewModel(application) {

    val result: LiveData<Result<ChallengeInfo, Exception>> = MutableLiveData()

    /**
     * Creates a challenge with the given arguments. The result is placed in [result]
     */
    fun createChallenge(gameName: String, playersCapacity: Int, roundNumber: Int) {
        val app = getApplication<DragApplication>()
        val mutableResult = result as MutableLiveData<Result<ChallengeInfo, Exception>>
        var capacity = playersCapacity
        when {
            capacity < MIN_PLAYERS -> capacity = MIN_PLAYERS
            capacity > MAX_PLAYERS -> capacity = MAX_PLAYERS
        }
        var roundNum = roundNumber
        when {
            roundNum < MIN_ROUNDS -> roundNum = MIN_ROUNDS
            roundNum > MAX_ROUNDS -> roundNum = MAX_ROUNDS
        }
        app.cloudRepository.publishChallenge(
                gameName = gameName,
                playersCapacity = capacity,
                roundNumber = roundNum,
                onSuccess = { mutableResult.value = Result(State.COMPLETE, result = it) },
                onError = { mutableResult.value = Result(State.COMPLETE, error = it) }
        )
    }
}