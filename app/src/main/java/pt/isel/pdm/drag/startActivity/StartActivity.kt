package pt.isel.pdm.drag.startActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityStartLayoutBinding

class StartActivity : AppCompatActivity() {

    private val binding: ActivityStartLayoutBinding by lazy { ActivityStartLayoutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_layout)

        val model = StartModel by viewModels()

        reactToPlayerCount(model)
        reactToRoundCount(model)
        setListeners(model)

        setContentView(binding.root)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners(model: StartModel) {
        binding.addPlayer.setOnTouchListener { _, _ ->
            model.addPlayer()
            reactToPlayerCount(model)
            true
        }
        binding.removePlayer.setOnTouchListener { _, _ ->
            model.removePlayer()
            reactToPlayerCount(model)
            true
        }

        binding.addRound.setOnTouchListener { _, _ ->
            model.addRound()
            reactToRoundCount(model)

            true
        }
        binding.removeRound.setOnTouchListener { _, _ ->
            model.removeRound()
            reactToRoundCount(model)
            true
        }
    }


    private fun reactToPlayerCount(startModel: StartModel) {
        val players = startModel.playerCount
        binding.playerCount.text = players.toString()
        binding.addPlayer.isVisible = players != MAX_PLAYERS
        binding.removePlayer.isVisible = players != MIN_PLAYERS
    }

    private fun reactToRoundCount(startModel: StartModel) {
        val rounds = startModel.roundCount
        binding.roundCount.text = rounds.toString()
        binding.addRound.isVisible = rounds != MAX_ROUNDS
        binding.removeRound.isVisible = rounds != MIN_ROUNDS
    }
}