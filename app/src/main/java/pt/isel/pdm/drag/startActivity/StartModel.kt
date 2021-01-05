package pt.isel.pdm.drag.startActivity

import androidx.lifecycle.ViewModel

const val MAX_PLAYERS = 10
const val MIN_PLAYERS = 2
const val MAX_ROUNDS = 10
const val MIN_ROUNDS = 1
private const val DEFAULT_PLAYER_COUNT = 6
private const val DEFAULT_ROUND_COUNT = 6

class StartModel(var playerCount : Int = DEFAULT_PLAYER_COUNT, var roundCount : Int = DEFAULT_ROUND_COUNT) : ViewModel() {

    var word = ""

    fun addPlayer(){
        playerCount++
    }

    fun removePlayer(){
        playerCount--
    }

    fun addRound(){
        roundCount++
    }

    fun removeRound(){
        roundCount--
    }
}

