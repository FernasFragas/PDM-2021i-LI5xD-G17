package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Aqui Implementamos com o pluggin do parcelize que nos permite criar uma representação externa do
 * objeto, para depois mais tarde a criar de novo, com o pluggin @Parcelize o codigo relacionado
 * com isso é realizado pelo sistema da android
 */
@Parcelize
enum class State : Parcelable { GUESSING, DRAWING, FINISHED }

@Parcelize
data class DragGame (var playersNum: Int = 0, var rounds: Int = 0) : Parcelable{

    lateinit var players: Array<DragDraw>
    var currentID = 0
    var currentWord = ""
    var roundCount = 0
    var state = State.DRAWING
    var timer = 0

    fun createDrawingContainer() {
        players = Array(playersNum) { DragDraw() }  //declaração de array
    }

    fun startNewDraw() {
        if (players[currentID].draws.size == 0)
            players[currentID] = DragDraw()
    }

    fun getCurrentDraw() = players[currentID]

    fun savePlayer() {
        ++currentID
    }

    fun addRound() {
        roundCount++
    }



}