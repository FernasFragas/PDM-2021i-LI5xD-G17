package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Position(val x: Float, val y: Float) : Parcelable {
    constructor(): this(0f,0f)
}

@Parcelize
data class Lines (val start: Position, val end: Position) : Parcelable

@Parcelize
class DragDraw(var draws: MutableList<Lines> = mutableListOf<Lines>(), var start : Position = Position()) : Parcelable{

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


