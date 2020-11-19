package pt.isel.pdm.drag.draw_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import pt.isel.pdm.drag.draw_activity.model.DragDraw
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.utils.runDelayed

private const val SAVED_STATE_KEY = "DragViewModel.SavedState"
var timer = 0

class DragViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    val game: MutableLiveData<DragGame> by lazy {
        MutableLiveData<DragGame>(savedState.get<DragGame>(SAVED_STATE_KEY) ?: DragGame())
    }

    fun startGame(playersNum: Int, rounds: Int) {
        if (game.value?.playersNum == 0) {
            game.value?.playersNum = playersNum
            game.value?.rounds = rounds
            game.value?.createDrawingContainer()
            setTimer()
        }
    }

    private fun setTimer() {
        val roundBeforeTimer = game.value?.roundCount
        val stateBeforeTimer = game.value?.state
        runDelayed(9999999999) {
            if (game.value?.roundCount == roundBeforeTimer && game.value?.state == stateBeforeTimer) {
                timer = 0
                changeState()
            }
        }
    }

    /**
     * verifica se é para desenhar ou advinhar
     */
    fun changeState() {
        if (game.value?.state == State.DRAWING) {
            game.value?.state = State.GUESSING
        } else {
            addRound()
            addPlayerDraw()
            if (game.value?.currentWord == "") {
                newWord("NO GUESS FOUND")
            }
            game.value?.state = State.DRAWING
        }
        game.value = game.value
        setTimer()
        savedState[SAVED_STATE_KEY] = game.value
    }


    /**
     * metodo talvez necessario para os varios jogadores
     */
    fun initiatePlayerDragDraw() {
        game.value?.startNewDraw()
    }

    /**
     * adiciona o desenho do jogador atual à lista de desenhos do jogo
     */
    fun addPlayerDraw() {
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
        //game.value?.save(dragDraw)
    }

    fun addRound() {
        game.value?.addRound()
    }

    fun initialWord(word:String) {
        if (game.value?.currentWord == "")
            newWord(word)
    }

    fun newWord(word: String) {
        game.value?.currentWord = word
    }

}