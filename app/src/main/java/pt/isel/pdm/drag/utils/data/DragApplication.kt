package pt.isel.pdm.drag.utils.data

import android.app.Application
import androidx.room.Room
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import pt.isel.pdm.drag.utils.data.fireRepository.FirestoreRepository
import pt.isel.pdm.drag.utils.data.localRepository.GameDataBase
import pt.isel.pdm.drag.utils.data.localRepository.GameRepository

/**
 * class where is created the repository, where is created the relational db and the cloud db
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

    val cloudRepository by lazy {
        FirestoreRepository(
            jacksonObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        )
    }
}