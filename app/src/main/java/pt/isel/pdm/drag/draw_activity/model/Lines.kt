package pt.isel.pdm.drag.model

data class Position(val x: Float, val y: Float) {
    constructor(): this(0f,0f)
}

data class Lines (val start: Position, val end: Position)

class Draws {
    private val draws = mutableListOf<Lines>()

    fun addDraws (line : Lines) {
        draws.add(line)
    }

    fun getDraws() : List<Lines> {
        return draws
    }
}


