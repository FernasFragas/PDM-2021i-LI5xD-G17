package pt.isel.pdm.drag.utils.data.localRepository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import pt.isel.pdm.drag.draw_activity.model.Lines
import pt.isel.pdm.drag.draw_activity.model.Position

/**
 * in this class is all the necessary logic for the DataBase of the repository
 */

@Database(entities = [GameResult.rounds::class, GameResult.currentRound::class], version = 1)
@TypeConverters(Converters::class)
abstract class GameDataBase : RoomDatabase() {

    abstract fun getGameResultsDao(): GameResultDao

}

/**
 * Functions for converting between memory and db and db and memory
 */
class Converters {

    @TypeConverter
    fun linesToString(lines: List<Lines>): String {
        return lines.fold(StringBuilder()){acc, lines ->
            acc.append("${lines.start.x},${lines.start.y}-${lines.end.x},${lines.end.y};")
        }.toString()
    }

    @TypeConverter
    fun linesFromString(stringLines :String): List<Lines> {
        return stringLines.split(';')
                .filter { it.isNotBlank() }
                .map {
                    val line = it.split('-')
                    var pair = line[0].split(',')
                    val start = Position(pair[0].toFloat(), pair[1].toFloat())
                    pair = line[1].split(',')
                    val end  = Position(pair[0].toFloat(), pair[1].toFloat())
                    Lines(start,end)
                }
    }

    
}