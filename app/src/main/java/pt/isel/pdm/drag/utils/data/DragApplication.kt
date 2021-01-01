package pt.isel.pdm.drag.utils.data

import android.app.Application
import androidx.room.Room

private const val GLOBAL_PREFS = "GlobalPreferences"

class DragApplication : Application() {

    val gameRepository by lazy {
        GameRepository(
                getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE),
                Room.databaseBuilder(this, GameDataBase::class.java, "GameDB").build()
        )
    }
}