package pt.isel.pdm.drag.startActivity

import androidx.lifecycle.ViewModel

const val MAX_PLAYERS = 10
const val MIN_PLAYERS = 5
const val MAX_ROUNDS = 10
const val MIN_ROUNDS = 5
private const val DEFAULT_PLAYER_COUNT = 6
private const val DEFAULT_ROUND_COUNT = 6

class StartModel(var playerCount : Int = DEFAULT_PLAYER_COUNT, var roundCount : Int = DEFAULT_ROUND_COUNT) : ViewModel() {

    fun addPlayer(){
        playerCount++
        if (playerCount > MAX_PLAYERS)
            playerCount = MAX_PLAYERS
    }

    fun removePlayer(){
        playerCount--
        if (playerCount < MIN_PLAYERS)
            playerCount = MIN_PLAYERS
    }

    fun addRound(){
        roundCount++
        if (roundCount > MAX_ROUNDS)
            roundCount = MAX_ROUNDS
    }

    fun removeRound(){
        roundCount--
        if (roundCount < MIN_ROUNDS)
            roundCount = MIN_ROUNDS
    }
    /*
    companion object {

        val instance: StartModel by lazy { StartModel() }
    }

     */
}

