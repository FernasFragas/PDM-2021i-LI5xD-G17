package pt.isel.pdm.drag.gameModeActvity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityGameModeBinding
import pt.isel.pdm.drag.draw_activity.local_Draw_Activity.DrawActivity
import pt.isel.pdm.drag.list_games_activity.ListGamesActivity
import pt.isel.pdm.drag.startActivity.StartActivity

class GameModeActivity : AppCompatActivity() {


    private val binding: ActivityGameModeBinding by lazy {
        ActivityGameModeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_mode)

        setContentView(binding.root)

        binding.offline.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

        binding.online.setOnClickListener {
            val intent = Intent(this, ListGamesActivity::class.java)
            startActivity(intent)
        }
    }
}