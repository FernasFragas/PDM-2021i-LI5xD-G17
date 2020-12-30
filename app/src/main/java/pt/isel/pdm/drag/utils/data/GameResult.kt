package pt.isel.pdm.drag.utils.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.isel.pdm.drag.draw_activity.model.Lines

class GameResult {

    @Entity(tableName = "rounds", primaryKeys = ["playerId", "roundId"])
    data class rounds(
            var roundId: Int,
            var playerId: Int,
            var lines: List<Lines>,
            var originalWord: String,
            var guessedWord : String
    )

    @Entity(tableName = "currentRound")
    data class currentRound(
            @PrimaryKey var playerId: Int,
            var lines: List<Lines>,
            var originalWord: String,
            var guessedWord : String

    )

}