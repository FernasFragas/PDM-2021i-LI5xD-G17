package pt.isel.pdm.drag.draw_activity.model

class DragGame (val playersNum: Int, val rounds: Int) {

    private val players = arrayOf<DragDraw>()



    fun savePlayer(id: Int, dragDraw: DragDraw) {
        players[id] = dragDraw
    }
}