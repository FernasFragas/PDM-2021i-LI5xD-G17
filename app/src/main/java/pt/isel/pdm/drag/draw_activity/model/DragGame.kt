package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Aqui Implementamos com o pluggin do parcelize que nos permite criar uma representação externa do
 * objeto, para depois mais tarde a criar de novo, com o pluggin @Parcelize o codigo relacionado
 * com isso é realizado pelo sistema da android
 */
@Parcelize
data class Player(var dragDraw : DragDraw = DragDraw(), var originalWord : String = "", var guessedWord : String = "") : Parcelable

@Parcelize
enum class State : Parcelable { GUESSING, DRAWING, FINISHED, NEW_ROUND, NOT_NEW_ROUND }

@Parcelize
data class DragGame(var playersNum: Int = 0,
                    var roundsNum: Int = 0,
                    var allDraws: MutableList<Array<Player>> = mutableListOf(),
                    var currentRound: Array<Player> = Array(playersNum) { Player() }
) : Parcelable{

    var currentID = 0
    var currentWord = ""
    var currentRoundNumber = 0
    var state = State.DRAWING
    var round = State.NEW_ROUND
    var timer = 0

    fun createDrawingContainer() {
        currentRound = Array(playersNum) { Player() }  //declaração de array
    }

    fun startNewDraw() {
        if (currentRound[currentID].dragDraw.draws.size == 0) {
            currentRound[currentID] = Player()
        }
    }

    fun getCurrentDraw() = currentRound[currentID].dragDraw

    fun savePlayer() {
        currentID = (currentID + 1) % playersNum
    }

    fun addRound() {
        allDraws.add(currentRound)
        currentRoundNumber++
        createDrawingContainer()
    }

    fun addOriginalWord(original : String) {
        currentRound[currentID].originalWord = original
    }

    fun addGuessedWord(guess : String) {
        currentRound[currentID].guessedWord = guess
    }

    fun getOriginal() = currentRound[currentID].originalWord

    fun getGuess() = currentRound[currentID].guessedWord





}