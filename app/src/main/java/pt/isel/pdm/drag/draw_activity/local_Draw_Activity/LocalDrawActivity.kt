package pt.isel.pdm.drag.draw_activity.local_Draw_Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.os.Bundle
import android.view.MotionEvent.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.LocalDragViewModel
import pt.isel.pdm.drag.utils.Keys
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.showActivity.ShowActivity
import pt.isel.pdm.drag.utils.ChallengeInfo

class LocalDrawActivity : AppCompatActivity() {

    private val binding: ActivityDrawBinding by lazy { ActivityDrawBinding.inflate(layoutInflater) }

    private val viewModel: LocalDragViewModel by viewModels()

    private val challenge: ChallengeInfo by lazy {
        intent.getParcelableExtra<ChallengeInfo>(Keys.CHALLENGE_INFO.name) ?:
        throw IllegalArgumentException("Mandatory extra ${Keys.CHALLENGE_INFO.name} not present")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.startGame(challenge.playerCapacity.toInt(), challenge.roundNum.toInt())
        viewModel.addOriginalWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())

        prepareListeners()

        viewModel.game.observe(this) {
            when (viewModel.game.value?.state) {
                State.DRAWING -> drawOnGoing()
                State.GUESSING -> guessState()
                State.FINISH_SCREEN -> finishState()
                State.NEW_ROUND -> newRoundState()
                State.CHANGE_ACTIVITY -> changeActivity()
            }
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, ShowActivity::class.java)
        intent.putExtra(Keys.GAME_KEY.name, viewModel.game.value!!)
        startActivity(intent)
    }

    private fun drawOnGoing() {
        binding.divider2.visibility = View.VISIBLE
        binding.dragDrawView.localViewModel = viewModel
        binding.userInput.visibility = View.GONE
        binding.hint.visibility = View.VISIBLE
        binding.gameOver?.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
        binding.hint.text = viewModel.getOriginalWord()
        binding.userInput.editText?.setText("")
        setDrawListener()
    }

    private fun guessState() {
        binding.divider2.visibility = View.VISIBLE
        binding.userInput.visibility = View.VISIBLE
        binding.hint.visibility = View.GONE
        binding.gameOver?.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }

    private fun newRoundState() {
        binding.divider2.visibility = View.GONE
        binding.userInput.visibility = View.GONE
        binding.hint.visibility = View.GONE
        binding.gameOver?.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
        binding.gameOver?.text = "Round " + viewModel.game.value?.currentRoundNumber
    }

    private fun finishState() {
        binding.divider2.visibility = View.GONE
        binding.userInput.visibility = View.GONE
        binding.hint.visibility = View.GONE
        binding.submitButton.visibility = View.GONE
        binding.gameOver?.visibility = View.VISIBLE
        binding.gameOver?.text = this.applicationContext.getText(R.string.Over)
    }

    private fun prepareListeners() {
        setDrawListener()
        setSubmitListener()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setDrawListener() {
        binding.dragDrawView.setOnTouchListener { v, event ->
            if (viewModel.game.value?.state == State.DRAWING) {
                val winWidth = binding.dragDrawView.width
                val winHeight = binding.dragDrawView.height
                when (event.action) {
                    ACTION_DOWN ->
                        viewModel.initiatePlayerLine(Position(event.x / winWidth, event.y / winHeight))
                    ACTION_MOVE -> {
                        viewModel.addPlayerLine(Position(event.x / winWidth, event.y / winHeight))
                        viewModel.initiatePlayerLine(Position(event.x / winWidth, event.y / winHeight))
                    }
                    ACTION_UP -> viewModel.addPlayerLine(Position(event.x / winWidth, event.y / winHeight))
                }
            }
            true
        }
    }


    private fun setSubmitListener() {
        binding.submitButton.setOnClickListener {
            if (viewModel.game.value!!.state == State.GUESSING)
                viewModel.addGuess(binding.userInput.editText?.text.toString())
            viewModel.changeState()
        }
    }


}