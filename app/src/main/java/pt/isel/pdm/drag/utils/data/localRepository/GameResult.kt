package pt.isel.pdm.drag.utils.data.localRepository

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.isel.pdm.drag.draw_activity.model.Lines

/**
 * class with all the entities necessary to save the game
 */
class GameResult {

    @Entity(tableName = "rounds")
    data class rounds(
            @PrimaryKey(autoGenerate = true) val id: Long = 0,
            val roundId: Int,
            val playerId: Int,
            val lines: List<Lines>,
            val originalWord: String,
            val guessedWord : String
    )

    @Entity(tableName = "currentRound")
    data class currentRound(
            @PrimaryKey val playerId: Int,
            val lines: List<Lines>,
            val originalWord: String,
            val guessedWord : String

    )

}