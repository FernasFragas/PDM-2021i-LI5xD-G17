package pt.isel.pdm.drag.draw_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.Keys
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.model.Position

/**
 * Activity referent for drawing
 */
class DrawActivity : AppCompatActivity() {

    private val binding: ActivityDrawBinding by lazy { ActivityDrawBinding.inflate(layoutInflater) }

    private var word = ""
    var timer = 0
    var c: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
        val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)
        word = intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString()

        var dragViewModel = DragViewModel(playerCount,roundCount)

        setContentView(binding.root)
        prepareListeners(dragViewModel)
        reactToState(dragViewModel)
        c = timer()
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
    private fun drawOnGoing(dragViewModel: DragViewModel) {
        dragViewModel.initiatePlayerDragDraw()
        binding.dragDrawView.viewModel = dragViewModel
        binding.userInput.visibility = View.INVISIBLE
        binding.hint?.visibility = View.VISIBLE
        binding.hint?.text = word
        setDrawListener(dragViewModel = dragViewModel)
    }

    /**
     * adequa a activity á opção de quando é para adivinhar
     */
    private fun guessState(dragViewModel: DragViewModel) {
        binding.userInput.visibility = View.VISIBLE
        binding.hint?.visibility = View.INVISIBLE
        setDrawListener(dragViewModel = dragViewModel)
    }


    /**
     * define qual o se é para desenhar ou para advinhar no estado seguinte
     */
    private fun reactToState(dragViewModel: DragViewModel){
        if (dragViewModel.drawingState)
            drawOnGoing(dragViewModel)
        else
            guessState(dragViewModel)
    }

    /**
     * Listeners
     */
    private fun prepareListeners(dragViewModel: DragViewModel) {
        setDrawListener(dragViewModel)
        setSubmitListener(dragViewModel)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDrawListener(dragViewModel: DragViewModel) {
        binding.dragDrawView.setOnTouchListener { v, event ->
            if (dragViewModel.drawingState) {
                when (event.action) {
                    ACTION_DOWN -> dragViewModel.initiatePlayerLine(Position(event.x, event.y))
                    ACTION_MOVE -> {
                        dragViewModel.addPlayerLine(Position(event.x, event.y))
                        dragViewModel.initiatePlayerLine(Position(event.x, event.y))
                    }
                    ACTION_UP -> dragViewModel.addPlayerLine(Position(event.x, event.y))
                }
            }
            true
        }
    }

    private fun setSubmitListener(dragViewModel: DragViewModel) {
        binding.submitButton.setOnClickListener {
            c?.onFinish()
            if (dragViewModel.drawingState) {
                dragViewModel.addPlayerDraw()
                binding.userInput.editText?.setText("")
            } else {
                word = binding.userInput.editText?.text.toString()
                //TODO ENVIAR O GUESS PARA O MODELO?
            }
            dragViewModel.changeState()

            reactToState(dragViewModel)
        }
    }
}