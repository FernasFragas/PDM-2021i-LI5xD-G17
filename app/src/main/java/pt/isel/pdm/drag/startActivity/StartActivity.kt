package pt.isel.pdm.drag.startActivity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.activity_start_layout.*
import pt.isel.pdm.drag.Keys
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityStartLayoutBinding
import pt.isel.pdm.drag.draw_activity.DrawActivity

//TODO{
//  1. por botão start enabled a false enquanto não estiver escolhida uma palavra
//  2. quando se escolhe uma palavra impedir que seja possivel escolher outra enquanto não se tira o check da atual
//  3. passar a palavra escolhida para a proxima activity
// }

class StartActivity : AppCompatActivity() {

    private val binding: ActivityStartLayoutBinding by lazy { ActivityStartLayoutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_layout)

        val viewModel by viewModels<StartModel>()

        setContentView(binding.root)

        reactToPlayerCount(viewModel)
        reactToRoundCount(viewModel)
        setListeners(viewModel)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners(model: StartModel) {
        binding.addPlayer.setOnClickListener {
            model.addPlayer()
            reactToPlayerCount(model)
        }

        binding.removePlayer.setOnClickListener {
            model.removePlayer()
            reactToPlayerCount(model)
        }

        binding.addRound.setOnClickListener {
            model.addRound()
            reactToRoundCount(model)
        }

        binding.removeRound.setOnClickListener {
            model.removeRound()
            reactToRoundCount(model)
        }

        binding.start.setOnClickListener {
            model.word = word.text.toString()
            val intent = Intent(this, DrawActivity::class.java).apply {
                putExtra(Keys.PLAYER_COUNT_KEY.name, model.playerCount)
                putExtra(Keys.ROUND_COUNT_KEY.name, model.roundCount)
                putExtra(Keys.GAME_WORD_KEY.name, model.word)
            }
            startActivity(intent)
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