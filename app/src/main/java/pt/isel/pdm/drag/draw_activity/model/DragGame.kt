package pt.isel.pdm.drag.draw_activity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class GameDTO(
        var playersNum: Int = 0,
        var roundsNum: Int = 0,
        var allDraws: MutableList<Array<Player>> = mutableListOf(),
        var currentRound: Array<Player> = Array(playersNum) { Player() },
        var currentID: Int = 0,
        var state: State = State.WAITING,
        var gameMode: Boolean = true,
        var currentRoundNumber: Int = 0
)

fun GameDTO.toGame() = DragGame(playersNum, roundsNum, allDraws, currentRound, currentID, state, gameMode, currentRoundNumber)

/**
 * Aqui Implementamos com o pluggin do parcelize que nos permite criar uma representação externa do
 * objeto, para depois mais tarde a criar de novo, com o pluggin @Parcelize o codigo relacionado
 * com isso é realizado pelo sistema da android
 */
@Parcelize
data class Player(var dragDraw : DragDraw = DragDraw(), var originalWord : String = "", var guessedWord : String = "") : Parcelable

@Parcelize
enum class State : Parcelable { GUESSING, DRAWING, FINISH_SCREEN, CHANGE_ACTIVITY,
    NEW_ROUND, WAITING }

@Parcelize
data class DragGame(var playersNum: Int = 0,
                    var roundsNum: Int = 0,
                    var allDraws: MutableList<Array<Player>> = mutableListOf(),
                    var currentRound: Array<Player> = Array(playersNum) { Player() },
                    var currentID: Int = 0,
                    var state: State = State.WAITING,
                    var gameMode: Boolean = true,
                    var currentRoundNumber: Int = 0
) : Parcelable{

    var currentWord = ""
    //var currentRoundNumber = 0
    //var state = State.WAITING
    var round = State.NEW_ROUND
    var timer = 0
    //var gameMode: Boolean = false

    /**
     * Creates an external representation of the game
     */
    fun toGameDTO() = GameDTO(playersNum, roundsNum, allDraws, currentRound, currentID, state, gameMode, currentRoundNumber)

    /**
     * Its created one per round, each round has one array with the size of the number of players
     * **/
    fun createDrawingContainer() {
        currentRound = Array(playersNum) { Player() }  //declaração de array
    }

    fun startNewDraw() {
        if (currentRound[currentID].dragDraw.draws.size == 0) {
            currentRound[currentID] = Player()
        }
    }

    fun getCurrentDraw() = currentRound[currentID].dragDraw

    fun getDraw(): DragDraw {

        if (!gameMode) return currentRound[currentID].dragDraw

        if (currentRound.size != 0) {
            if (gameMode) {
                if (state == State.GUESSING) {
                    return getDrawBefore()
                }
            }
            return currentRound[currentID].dragDraw
        }


        return DragDraw()
    }

    fun getDrawBefore(): DragDraw {
        var id = currentID -1
        if (id<0) id = 1
        return currentRound[id].dragDraw
    }

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