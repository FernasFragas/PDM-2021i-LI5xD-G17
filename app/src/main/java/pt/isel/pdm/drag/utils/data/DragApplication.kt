package pt.isel.pdm.drag.utils.data

import android.app.Application
import androidx.room.Room

/**
 * class where is created the repository, where is created the db
 */
private const val GLOBAL_PREFS = "GlobalPreferences"

class DragApplication : Application() {

    val gameRepository by lazy {
        GameRepository(
                getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE),
                Room.databaseBuilder(this, GameDataBase::class.java, "GameDB")
                        .fallbackToDestructiveMigration()
                        .build()
        )
    }
}