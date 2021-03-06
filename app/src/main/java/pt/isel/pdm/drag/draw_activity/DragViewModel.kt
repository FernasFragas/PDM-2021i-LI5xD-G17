package pt.isel.pdm.drag.draw_activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.MyOnlineID
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.utils.ChallengeInfo
import pt.isel.pdm.drag.utils.Result
import pt.isel.pdm.drag.utils.Timers
import pt.isel.pdm.drag.utils.data.DragApplication
import pt.isel.pdm.drag.utils.runDelayed
import java.lang.Exception


/**
 * no construtor desta classe vai ser passado uma instancia de um tipo que tem um estado da activity
 * que foi preservado,(está relacionado com a lifecycle)
 *
 * no construtor é tambem passado a instancia application onde é criado a repo com a db
 */

private const val SAVED_STATE_KEY = "DragViewModel.SavedState"

/*
class DragViewModel(
        application: Application,
        private val savedState: SavedStateHandle,
): AndroidViewModel(application) {
 */
class DragViewModel(
        application: Application,
        //private val savedState: SavedStateHandle,
        val challengeInfo: ChallengeInfo
): AndroidViewModel(application) {

    val game: LiveData<DragGame> = MutableLiveData(DragGame())

    private val subscription = getApplication<DragApplication>().cloudRepository.subscribeTo(
            challengeInfo.id,
            onSubscriptionError = { TODO() },
            onStateChanged = {
                (game as MutableLiveData<DragGame>).value = it
            }
    )

    override fun onCleared() {
        super.onCleared()
        subscription.remove()
    }


    /**
     * representations of the game in the local repository
     */
    val gameRepo by lazy {
        getApplication<DragApplication>().gameRepository
    }

    /**
     * representations of the game in the cloud
     */
    val gameCloudRepo by lazy {
        getApplication<DragApplication>().cloudRepository
    }


    fun updateCloudGame() {
        gameCloudRepo.updateGameState(
                game.value?: throw IllegalStateException(),
                challengeInfo,
                onSuccess = { (game as MutableLiveData<DragGame>).value = it},
                onError = { TODO()}
        )
    }

    /**
     * passa os valores que vêm da startActivity, para a nossa representação logica do jogo
     * quando estamos a iniciar um novo jogo
     */
    fun startGame(playersNum: Int, rounds: Int, gameMode: Boolean) {
        if (game.value?.playersNum == 0) {
            game.value?.playersNum = playersNum
            game.value?.roundsNum = rounds
            game.value?.createDrawingContainer()
            //game.value = game.value
            //savedState[SAVED_STATE_KEY] = game.value
            //setTimer()
        }
        game.value?.state = State.NEW_ROUND
        setTimer()
        changeState()
    }

    fun setTimer() {
        val roundBeforeTimer = game.value?.currentRoundNumber
        val idBeforeTimer = game.value?.currentID
        val stateBeforeTimer = game.value?.state
        var seconds = when (stateBeforeTimer){
            State.DRAWING -> Timers.DRAWING_TIME.time
            State.GUESSING -> Timers.GUESSING_TIME.time
            State.FINISH_SCREEN -> Timers.FINISH_TIME.time
            State.NEW_ROUND -> Timers.NEW_ROUND_TIME.time
            else -> 0
        }
        runDelayed(seconds * 1000L) {
            if (game.value?.currentID == idBeforeTimer
                    && game.value?.state == stateBeforeTimer
                    && game.value?.currentRoundNumber == roundBeforeTimer
                    && game.value?.state != State.CHANGE_ACTIVITY) {
                game.value?.timer = -1 //TODO TIRAR O NUMERO MAGICO
                changeState()
            }
        }
    }



    /**
     * verifica se é para desenhar ou advinhar
     */
    fun changeState() {
        //se o jogo for online faz isto
        gameRepo.myState = game.value?.state!!.ordinal
        when (game.value?.state) {
            State.DRAWING -> {
                game.value?.state = State.GUESSING
                addPlayerID()
            }


            State.GUESSING -> {
                var word = getGuess()
                if (word == "") {
                    word = "NO GUESS FOUND"
                    game.value?.addGuessedWord(word)
                }
                addPlayerID()

                updateGuessingState()

                game.value?.addOriginalWord(word)

            }
            State.NEW_ROUND -> {
                game.value?.state = State.DRAWING
            }
            State.FINISH_SCREEN -> {
                gameRepo.saveGame(game.value!!)     //save game in the dp of repository
                game.value?.state = State.CHANGE_ACTIVITY
            }
            State.WAITING -> {
                game.value?.state = State.NEW_ROUND
            }

        }
        //}

        //if(game.value?.gameMode!!)
        //game.value = game.value
        //savedState[SAVED_STATE_KEY] = game.value
        setTimer()
        updateCloudGame()
    }

    private fun isFinished() = game.value?.currentRoundNumber == game.value?.roundsNum

    private fun updateGuessingState() {
        if (game.value?.currentID == 0) {
            addRound()
            updateCloudGame()
            if (isFinished())
                game.value?.state = State.FINISH_SCREEN
            else
                game.value?.state = State.NEW_ROUND
        } else {
            game.value?.state = State.DRAWING
        }
    }


    /**
     * forfeits the game
     */
    fun forfeit() {
        game.value?.state = State.FINISH_SCREEN
        updateCloudGame()
        gameCloudRepo.deleteGame(challengeInfo)
    }

    /**
     * incrementa o id do player
     */
    fun addPlayerID() {
        game.value?.savePlayer()
    }

    /**
     * inicia uma linha desenhada pelo jogador
     */
    fun initiatePlayerLine(start: Position) {
        game.value?.getCurrentDraw()?.initiateDraw(start)
    }

    /**
     * adiciona uma linha ao desenho do jogador atual
     */
    fun addPlayerLine(end: Position) {
        game.value?.getCurrentDraw()?.addLines(end)
    }

    /**
     * incrementa o valor da ronda na nossa representação logica do jogo
     */
    fun addRound() {
        gameRepo.saveGame(game.value!!)     //save game in the dp of repository
        game.value?.addRound()
    }

    /**
     * caso a nossa representação logica não tenha palavra é atribuida a palavra recebida
     */
    fun initialWord(word:String) {
        if (game.value?.getOriginal() == "")
            addOriginalWord(word)
    }

    /**
     * atualiza a palavra atual no modelo logico
     */
    fun addGuess(word: String) {
        game.value?.addGuessedWord(word)
    }

    fun addOriginalWord(original : String) {
        game.value?.addOriginalWord(original)
    }

    fun getOriginalWord() = game.value?.getOriginal()

    fun getGuess() = game.value!!.getGuess()

    fun setGameMode(mode: Boolean) {game.value?.gameMode = mode}

}