package pt.isel.pdm.drag.draw_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.model.DragDraw
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.utils.runDelayed

private const val SAVED_STATE_KEY = "DragViewModel.SavedState"

class DragViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    val game: MutableLiveData<DragGame> by lazy {
        MutableLiveData<DragGame>(savedState.get<DragGame>(SAVED_STATE_KEY) ?: DragGame())
    }
    var dragDraw: DragDraw = DragDraw()


    fun createNewGame(playersNum: Int, rounds: Int) {
        game.value = DragGame(playersNum, rounds)
        setTimer()
    }

    private fun setTimer() {
        val roundBeforeTimer = game.value?.roundCount
        val stateBeforeTimer = game.value?.state
        runDelayed(6000) {
            if (game.value?.roundCount == roundBeforeTimer && game.value?.state == stateBeforeTimer)
                changeState()
        }
    }

    /**
     * verifica se é para desenhar ou advinhar
     */
    fun changeState() {
        if (game.value?.state == State.DRAWING) {
            addPlayerDraw()
            game.value?.state = State.GUESSING
        } else {
            addRound()
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
        dragDraw = DragDraw()

    }

    /**
     * adiciona o desenho do jogador atual à lista de desenhos do jogo
     */
    fun addPlayerDraw() {
        game.value?.savePlayer(dragDraw)
    }

    /**
     * inicia uma linha desenhada pelo jogador
     */
    fun initiatePlayerLine(start: Position) {
        dragDraw.initiateDraw(start)
    }

    /**
     * adiciona uma linha ao desenho do jogador atual
     */
    fun addPlayerLine(end: Position) {
        dragDraw.addLines(end)
        game.value?.save(dragDraw)
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