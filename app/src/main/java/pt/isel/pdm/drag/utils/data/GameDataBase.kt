package pt.isel.pdm.drag.utils.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import pt.isel.pdm.drag.draw_activity.model.Player

@Database(entities = [GameResult::class], version = 1)
@TypeConverters(Converters::class)
abstract class GameDataBase : RoomDatabase() {

    abstract fun getGameResultsDao(): GameResultDao
}

class Converters {

    @TypeConverter
    fun linesToString(playerList: MutableList<Array<Player>>) {

    }

    @TypeConverter
    fun linesFromString(lines :String) {

    }
}