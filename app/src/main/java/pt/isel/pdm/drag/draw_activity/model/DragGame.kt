package pt.isel.pdm.drag.draw_activity.model

class DragGame (val playersNum: Int, val rounds: Int) {

    private val players = Array(playersNum) { DragDraw() }  //declaração de array
    var currentID = 0


    fun savePlayer(dragDraw: DragDraw) {
        players[currentID] = dragDraw
        ++currentID
    }
}