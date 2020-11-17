package pt.isel.pdm.drag.draw_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.Keys
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.model.Position

//TODO{
//  1. Definir o que se faz no botão submit
//  2. regra de quando é para mostrar a pista e desenhar ou quando é para mostrar o desenho e adivinhar
//  3. ver todos da dragdrawview
// }


/**
 * Activity referent for drawing
 */
class DrawActivity : AppCompatActivity() {

    private val binding: ActivityDrawBinding by lazy { ActivityDrawBinding.inflate(layoutInflater) }

    private var word = "carro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
        val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)

        var dragViewModel = DragViewModel(playerCount,roundCount)

        setContentView(binding.root)

        prepareListeners(dragViewModel)
        reactToState(dragViewModel)
    }

    /**
     *
     */
    private fun drawOnGoing(dragViewModel: DragViewModel) {
        dragViewModel.initiatePlayerDragDraw()
        binding.dragDrawView.viewModel = dragViewModel
        binding.userInput.visibility = View.INVISIBLE
        binding.hint?.visibility = View.VISIBLE
        binding.hint?.text = word
        setDrawListener(dragViewModel = dragViewModel)
    }

    private fun guessState(dragViewModel: DragViewModel) {
        binding.userInput.visibility = View.VISIBLE
        binding.hint?.visibility = View.INVISIBLE
        setDrawListener(dragViewModel = dragViewModel)
    }

    private fun reactToState(dragViewModel: DragViewModel){
        if (dragViewModel.drawingState)
            drawOnGoing(dragViewModel)
        else
            guessState(dragViewModel)
    }

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