package pt.isel.pdm.drag.utils.data.localRepository

import android.content.SharedPreferences
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.State
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Keys use for saving the respective values in the game
 */
private const val NUMBER_OF_PLAYERS = "NumberOfPlayers"
private const val NUMBER_OF_ROUNDS = "NumberOfPRounds"
private const val MY_CURRENT_STATE = "currentState"
private const val MY_LOCAL_ID = "localID"
/**
 *
 * App repository contains information about the game
 *
 * all the functions needed to operato with the db are in this class
 *
 */
class GameRepository (
        private val sharedPreferences: SharedPreferences,
        private val db: GameDataBase
) {

    /**
     * Allow us to save and load the data of the game to the db without slow down the main thread
     * with the process of accessing the db, by use this thread for that propose
     * (not sure about this last part)
     */
    companion object{
        private val worker: Executor = Executors.newSingleThreadExecutor()
    }

    /**
     * Number of players
     * @NUMBER_OF_PLAYERS- represents the key for search
     * @1- represents the default number when the search return null
     */
    var playersNum: Int
        get() = sharedPreferences.getInt(NUMBER_OF_PLAYERS,1)
        private set(value) {
            sharedPreferences
                    .edit()
                    .putInt(NUMBER_OF_PLAYERS,value)
                    .apply()
        }

    /**
     * Number of rounds
     */
    var roundsNum: Int
        get() = sharedPreferences.getInt(NUMBER_OF_ROUNDS,1)
        private set(value) {
            sharedPreferences
                    .edit()
                    .putInt(NUMBER_OF_ROUNDS,value)
                    .apply()
        }

    /**
     * last state before waiting, guarda o valor ordinal do estado que corresponde à sua posição no enum
     */
    var myState: Int
        get() = sharedPreferences.getInt(MY_CURRENT_STATE,1)
        set(value) {
            sharedPreferences
                    .edit()
                    .putInt(MY_CURRENT_STATE,value)
                    .apply()
        }


    /**
     * last state before waiting, guarda o valor ordinal do estado que corresponde à sua posição no enum
     */
    var localID: Int
        get() = sharedPreferences.getInt(MY_LOCAL_ID,0)
        set(value) {
            sharedPreferences
                    .edit()
                    .putInt(MY_LOCAL_ID,value)
                    .apply()
        }

    /**
     *Saves all rounds of the game separately, where all player game will be register and identify by
     * the duo key: roundId and playerId
     */
    fun saveGame(game: DragGame) {
        var round = 0
        var player = 0
        var list = game.allDraws
        worker.execute {
            list.forEach() {
                it.forEach {
                    db.getGameResultsDao().insertRounds(GameResult.rounds(
                            roundId = round,
                            playerId = player,
                            lines = it.dragDraw.getLines(),
                            originalWord = it.originalWord,
                            guessedWord = it.guessedWord
                    ))
                    player++
                }
                round++
            }
        }
        playersNum = game.playersNum
        roundsNum = game.roundsNum
    }

    /*
    fun saveCurrentRound(currentRound: Array<Player>) {
        worker.execute {
            currentRound.forEach {
                db.getGameResultsDao().
            }
        }
    }*/

    /**
     *
     */
    fun loadGame() {
        
    }
}