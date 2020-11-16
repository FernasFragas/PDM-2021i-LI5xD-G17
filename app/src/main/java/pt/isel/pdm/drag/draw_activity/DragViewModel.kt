package pt.isel.pdm.drag.draw_activity

import androidx.lifecycle.ViewModel
import pt.isel.pdm.drag.draw_activity.model.DragDraw
import pt.isel.pdm.drag.draw_activity.model.Position

class DragViewModel : ViewModel() {

    var dragDraw: DragDraw = DragDraw()

    /**
     * metodo talvez necessario para os varios jogadores
     */
    fun initiatePlayerDragDraw() {
        dragDraw = DragDraw()
    }

    /**
     * adiciona o desenho do jogador atual à lista de desenhos do jogo
     */
    fun addPlayerDraw() {}

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