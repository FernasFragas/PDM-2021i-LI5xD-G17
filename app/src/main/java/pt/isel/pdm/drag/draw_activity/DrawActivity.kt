package pt.isel.pdm.drag.draw_activity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
        val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)


        var dragViewModel = DragViewModel(playerCount,roundCount)


        setContentView(binding.root)
        drawOnGoing(dragViewModel)
        submitPlayer(dragViewModel)
    }

    /**
     *
     */
    private fun drawOnGoing(dragViewModel: DragViewModel) {
        dragViewModel.initiatePlayerDragDraw()
        binding.dragDrawView.viewModel = dragViewModel
        binding.InputView4.visibility = View.INVISIBLE
        binding.Pista4.visibility = View.VISIBLE
        binding.Pista4.text = "carro"
        binding.dragDrawView.setOnTouchListener { v, event ->
            when(event.action) {
                ACTION_DOWN -> dragViewModel.initiatePlayerLine(Position(event.x,event.y))
                ACTION_MOVE -> {
                    dragViewModel.addPlayerLine(Position(event.x,event.y))
                    dragViewModel.initiatePlayerLine(Position(event.x,event.y))
                }
                ACTION_UP -> dragViewModel.addPlayerLine(Position(event.x,event.y))
            }
            true
        }
    }

    /**
     *
     */
    private fun submitPlayer(dragViewModel: DragViewModel) {
        binding.submitButton.setOnClickListener{
            dragViewModel.addPlayerDraw()
            dragViewModel.verifyPlayerAndRound()
            drawOnGoing(dragViewModel)
        }
    }
}