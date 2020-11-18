package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


enum class State { GUESSING, DRAWING }

@Parcelize
data class DragGame (val playersNum: Int = 0, val rounds: Int = 0) : Parcelable{

    val players = Array(playersNum) { DragDraw() }  //declaração de array
    var currentID = 0
    var currentWord = ""
    var roundCount = 0
    var state = State.DRAWING


    fun save(dragDraw: DragDraw) {
        players[currentID] = dragDraw
    }

    fun savePlayer(dragDraw: DragDraw) {
        save(dragDraw)
        ++currentID
    }

    fun addRound() {
        roundCount++
    }



}