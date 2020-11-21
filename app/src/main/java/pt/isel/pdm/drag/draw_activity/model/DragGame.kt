package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Aqui Implementamos com o pluggin do parcelize que nos permite criar uma representação externa do
 * objeto, para depois mais tarde a criar de novo, com o pluggin @Parcelize o codigo relacionado
 * com isso é realizado pelo sistema da android
 */
@Parcelize
enum class State : Parcelable { GUESSING, DRAWING, FINISHED, NEW_ROUND, CURRENT_ROUND }

@Parcelize
data class DragGame(var playersNum: Int = 0,
                    var roundsNum: Int = 0,
                    var allDraws: MutableList<Array<DragDraw>> = mutableListOf(),
                    var currentRound: Array<DragDraw> = Array(playersNum) { DragDraw() }
) : Parcelable{

    var currentID = 0
    var currentWord = ""
    var currentRoundNumber = 0
    var state = State.DRAWING
    var round = State.NEW_ROUND
    var timer = 0

    fun createDrawingContainer() {
        currentRound = Array(playersNum) { DragDraw() }  //declaração de array
    }

    fun startNewDraw() {
        if (currentRound[currentID].draws.size == 0)
            currentRound[currentID] = DragDraw()
    }

    fun getCurrentDraw() = currentRound[currentID]

    fun savePlayer() {
        currentID = (currentID + 1) % playersNum
    }

    fun addRound() {
        allDraws.add(currentRound)
        currentRoundNumber++
        createDrawingContainer()
    }



}