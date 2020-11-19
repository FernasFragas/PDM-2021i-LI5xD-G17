package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Position(val x: Float, val y: Float) : Parcelable {
    constructor(): this(0f,0f)
}

@Parcelize
data class Lines (val start: Position, val end: Position) : Parcelable


class DragDraw {

    var draws = mutableListOf<Lines>()
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

    fun invertDraw(isHorizontal: Boolean): Boolean = adjustCoordinates(isHorizontal)

    /**
     * ajusta a coordenadas dos pontos e
     */
    fun adjustCoordinates(isHorizontal: Boolean): Boolean {
        val newDraws = mutableListOf<Lines>()
            draws.forEach {
                val start = it.start
                val startPosition = Position(start.x, start.y)
                val end = it.end
                val endPosition = Position(end.x, end.y)
                newDraws.add(Lines(endPosition, startPosition))
            }
            draws = newDraws
            return true
    }
/*
    fun calcPos(pos: Position): Position {
        val ratio = 8*8
        var diagonal = sqrt(pos.x.toDouble().pow(2.0) + pos.y.toDouble().pow(2.0))
        var height = diagonal / sqrt(ratio+1.0)
        var with = ratio * diagonal / sqrt(ratio+1.0)
        return Position(with.toFloat(), height.toFloat())
    }
*/
}


