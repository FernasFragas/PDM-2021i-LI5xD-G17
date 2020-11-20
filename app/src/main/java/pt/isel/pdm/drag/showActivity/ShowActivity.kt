package pt.isel.pdm.drag.showActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import pt.isel.pdm.drag.Keys
import pt.isel.pdm.drag.databinding.ActivityShowBinding
import pt.isel.pdm.drag.draw_activity.DragViewModel
import pt.isel.pdm.drag.draw_activity.model.DragDraw
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position

private const val SWIPE_RANGE = 40

class ShowActivity : AppCompatActivity() {

    //TODO: ANTES DE CHAMAR ESTA ACTIVITY POR O CURRENTID = 0

    private val binding: ActivityShowBinding by lazy { ActivityShowBinding.inflate(layoutInflater) }
    private val viewModel: DragViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = intent.getParcelableExtra<DragGame>(Keys.GAME_KEY.name)
        viewModel.game.value?.players = model!!.players
        binding.showDraw.viewModel = viewModel
        setContentView(binding.root)

        setListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {

        val model = ShowModel()

        binding.showDraw.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> model.addStartPos(event.x, event.y)
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_UP -> {
                    model.addEndPos(event.x, event.y)
                    val state = model.getSwipeState()
                    doSwipe(state)
                }
            }
            true
        }
    }

    enum class SwipeState { LEFT, RIGHT, NONE }

    class ShowModel {

        lateinit var start : Position
        lateinit var end : Position

        fun addStartPos(x : Float, y : Float) {
            start = Position(x, y)
        }

        fun addEndPos(x : Float, y : Float) {
            end = Position(x, y)
        }

        fun getSwipeState() : SwipeState{
            val horizontalDifference = start.x - end.x
            return when {
                horizontalDifference > SWIPE_RANGE -> SwipeState.LEFT
                horizontalDifference < SWIPE_RANGE -> SwipeState.RIGHT
                else -> SwipeState.NONE
            }
        }
    }

    private fun doSwipe(swipeState: SwipeState) {

        val game = viewModel.game.value!!

        when(swipeState) {
            SwipeState.LEFT -> {
                if (game.currentID != 0)
                    game.currentID = game.currentID - 1
            }
            SwipeState.RIGHT -> {
                if (game.currentID != game.rounds - 1)
                    game.currentID = game.currentID + 1
            }
        }
    }
}