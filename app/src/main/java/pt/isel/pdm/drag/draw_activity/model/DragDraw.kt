package pt.isel.pdm.drag.draw_activity.model

data class Position(val x: Float, val y: Float) {
    constructor(): this(0f,0f)
}

data class Lines (val start: Position, val end: Position)

class DragDraw {

    val draws = mutableListOf<Lines>()
    var start = Position()

    fun initiateDraw (s: Position) {
        start = s
    }

    fun addLines (end : Position) {
        val line = Lines(start, end)
        draws.add(line)
    }

    fun getLines() : List<Lines> {
        return draws
    }
}


