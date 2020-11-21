package pt.isel.pdm.drag.draw_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.utils.runDelayed

/**
 * no construtor desta classe vai ser passado uma instancia de um tipo que tem um estado da activity
 * que foi preservado,(está relacionado com a lifecycle)
 */

private const val SAVED_STATE_KEY = "DragViewModel.SavedState"


class DragViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    val game: MutableLiveData<DragGame> by lazy {
        MutableLiveData<DragGame>(savedState.get<DragGame>(SAVED_STATE_KEY) ?: DragGame())
    }
/*
    val time: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    fun changeTimer(millis: Long) {
        /*
        if(time.value == false) {
            runDelayed(millis) {
                game.value?.timer = game.value?.timer!! + 1
                time.value = true
            }
        } else { time.value = false }
        */
        /*
        runDelayed(millis) {
            game.value?.timer = game.value?.timer!! + 1
            time.value = time.value!! + 1
            savedState[SAVED_STATE_KEY] = game.value
            changeTimer(millis)
        }

         */

    }
*/
    /**
     * passa os valores que vêm da startActivity, para a nossa representação logica do jogo
     * quando estamos a iniciar um novo jogo
     */
    fun startGame(playersNum: Int, rounds: Int) {
        if (game.value?.playersNum == 0) {
            game.value?.playersNum = playersNum
            game.value?.roundsNum = rounds
            game.value?.createDrawingContainer()
            game.value = game.value
            savedState[SAVED_STATE_KEY] = game.value
            setTimer()
        }
    }

    private fun setTimer() {
        val roundBeforeTimer = game.value?.currentRoundNumber
        val idBeforeTimer = game.value?.currentID
        val stateBeforeTimer = game.value?.state
        runDelayed(6000L) {
            if (game.value?.currentID == idBeforeTimer
                    && game.value?.state == stateBeforeTimer
                    && game.value?.currentRoundNumber == roundBeforeTimer) {
                game.value?.timer = -1 //TODO TIRAR O NUMERO MAGICO
                changeState()
            }
        }
    }

    /**
     * verifica se é para desenhar ou advinhar
     */
    fun changeState() {
        if (game.value?.state == State.DRAWING) {
            game.value?.currentWord = ""
            game.value?.state = State.GUESSING
        } else if (game.value?.state == State.GUESSING) {
            addPlayerID()
            /*
            if (game.value?.currentWord == "") {
                game.value?.addGuess("NO GUESS FOUND")
            }
             */
            if (game.value?.getGuess() == "") {
                game.value?.addGuessedWord("NO GUESS FOUND")
            }

            if (game.value?.currentID == 0) {
                addRound()
                game.value?.round = State.NEW_ROUND
            }

            if (game.value?.currentRoundNumber == game.value?.roundsNum)
                game.value?.state = State.FINISHED
            else
                game.value?.state = State.DRAWING
        }
        game.value = game.value
        savedState[SAVED_STATE_KEY] = game.value
        setTimer()
    }


    /**
     * metodo talvez necessario para os varios jogadores
     */
    fun initiatePlayerDragDraw() {
        game.value?.startNewDraw()
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
     * incrementa o valor da rounda na nossa representação logica do jogo
     */
    fun addRound() {
        game.value?.addRound()
    }

    /**
     * caso a nossa representação logica não tenha palavra é atribuida a palavra recebida
     */
    fun initialWord(word:String) {
        if (game.value?.getOriginal() == "")
        //if (game.value?.currentWord == "")
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

    fun getGuess() = game.value?.getGuess()

}