package pt.isel.pdm.drag.draw_activity

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import pt.isel.pdm.drag.draw_activity.model.DragDraw
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position

class DragViewModel(playersnum: Int, rounds: Int) : ViewModel() {

    var dragDraw: DragDraw = DragDraw()
    val dragGame = DragGame(playersnum,rounds)

    var drawingState = true


    /**
     * verifica se é para desenhar ou advinhar
     */
    fun changeState() {
        drawingState = !drawingState
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
        dragGame.savePlayer(dragDraw)
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
    }

}