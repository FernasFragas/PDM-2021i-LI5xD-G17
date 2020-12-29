package pt.isel.pdm.drag.utils.data

import android.content.SharedPreferences

private const val NUMBER_OF_PLAYERS = "NumberOfPlayers"
private const val NUMBER_OF_ROUNDS = "NumberOfPRounds"


class GameRepository (
        private val sharedPreferences: SharedPreferences,
        private val db: GameDataBase
) {

    /**
     * Number of players
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
}