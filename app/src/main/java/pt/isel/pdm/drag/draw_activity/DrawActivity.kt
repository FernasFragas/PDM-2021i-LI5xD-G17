package pt.isel.pdm.drag.draw_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent.*
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.Keys
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.utils.runDelayed

/**
 * Activity referent for drawing
 */
class DrawActivity : AppCompatActivity() {

    private val binding: ActivityDrawBinding by lazy { ActivityDrawBinding.inflate(layoutInflater) }
    private val viewModel: DragViewModel by viewModels()

    var timer = 0
    var c: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
        val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)

        viewModel.createNewGame(playerCount, roundCount)
        viewModel.initialWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())

        setContentView(binding.root)
        prepareListeners()


        viewModel.game.observe(this) {
            when (viewModel.game.value?.state) {
                State.DRAWING -> drawOnGoing()
                State.GUESSING -> guessState()
            }
        }
        //c = timer()
    }


    fun timer(): CountDownTimer {
        timer=0
       return object : CountDownTimer(61000,1000) {
            override fun onTick(p0: Long) {
                binding.counter?.setText(timer.toString())
                ++timer
            }

            override fun onFinish() {
            }
        }.start()
    }

    /**
     *  adequa a activity á opção de quando é para desenhar
     */
    private fun drawOnGoing() {
        viewModel.initiatePlayerDragDraw()
        binding.dragDrawView.viewModel = viewModel
        binding.userInput.visibility = View.INVISIBLE
        binding.hint.visibility = View.VISIBLE
        binding.hint.text = viewModel.game.value?.currentWord
        setDrawListener()
    }

    /**
     * adequa a activity á opção de quando é para adivinhar
     */
    private fun guessState() {
        binding.userInput.visibility = View.VISIBLE
        binding.hint.visibility = View.INVISIBLE
    }

    /**
     * Listeners
     */
    private fun prepareListeners() {
        setDrawListener()
        setSubmitListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDrawListener() {
        binding.dragDrawView.setOnTouchListener { v, event ->
            if (viewModel.game.value?.state == State.DRAWING) {
                when (event.action) {
                    ACTION_DOWN -> viewModel.initiatePlayerLine(Position(event.x, event.y))
                    ACTION_MOVE -> {
                        viewModel.addPlayerLine(Position(event.x, event.y))
                        viewModel.initiatePlayerLine(Position(event.x, event.y))
                    }
                    ACTION_UP -> viewModel.addPlayerLine(Position(event.x, event.y))
                }
            }
            true
        }
    }

    private fun setSubmitListener() {
        binding.submitButton.setOnClickListener {
            c?.onFinish()
            changeState()
        }
    }

    private fun changeState() {
        if (viewModel.game.value?.state == State.DRAWING)
            binding.userInput.editText?.setText("")
        else
            viewModel.newWord(binding.userInput.editText?.text.toString())
        viewModel.changeState()
    }
}