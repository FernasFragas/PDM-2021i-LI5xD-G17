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
        if (!isHorizontal) {
            draws.forEach {
                val start = it.start
                val startPosition = Position(start.x + 188, start.y - 272)
                val end = it.end
                val endPosition = Position(end.x + 188, end.y - 272)
                newDraws.add(Lines(endPosition, startPosition))
            }
            draws = newDraws
            return true
        }
        draws.forEach {
            val start = it.start
            val startPosition = Position(start.x - 188, start.y + 272)
            val end = it.end
            val endPosition = Position(end.x - 188, end.y + 272)
            newDraws.add(Lines(endPosition, startPosition))
            }
        draws = newDraws
        return false
    }
}


