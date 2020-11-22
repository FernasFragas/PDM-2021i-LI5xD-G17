package pt.isel.pdm.drag.showActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_show.view.*
import pt.isel.pdm.drag.utils.Keys
import pt.isel.pdm.drag.databinding.ActivityShowBinding
import pt.isel.pdm.drag.draw_activity.DragViewModel
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position

private const val SWIPE_RANGE = 40

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

    fun getSwipeState() : SwipeState {
        if (start.x >= end.x) {
            if (start.x - end.x > SWIPE_RANGE)
                return SwipeState.LEFT
        } else if (end.x - start.x > SWIPE_RANGE)
            return SwipeState.RIGHT
        return SwipeState.NONE
    }
}

class ShowActivity : AppCompatActivity() {

    private val binding: ActivityShowBinding by lazy { ActivityShowBinding.inflate(layoutInflater) }
    private val viewModel: DragViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.showDraw.viewModel = viewModel

        val model = intent.getParcelableExtra<DragGame>(Keys.GAME_KEY.name)
        viewModel.game.value?.allDraws = model!!.allDraws
        viewModel.game.value?.currentRound = model.allDraws[model.currentRoundNumber]
        viewModel.game.value?.playersNum = model.playersNum
        viewModel.game.value?.roundsNum = model.roundsNum
        updateVisually()


        setListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {

        val model = ShowModel()

        binding.showDraw.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> model.addStartPos(event.x, event.y)
                MotionEvent.ACTION_UP -> {
                    model.addEndPos(event.x, event.y)
                    val state = model.getSwipeState()
                    doSwipe(state)
                }
            }
            true
        }
    }


    private fun doSwipe(swipeState: SwipeState) {

        val game = viewModel.game.value!!

        when(swipeState) {
            SwipeState.RIGHT -> {
                if (game.currentID != 0) {
                    game.currentID = game.currentID - 1
                    updateVisually()
                }
            }
            SwipeState.LEFT -> {
                if (game.currentID != game.playersNum - 1) {
                    game.currentID = game.currentID + 1
                    updateVisually()
                }
                else {
                    game.currentRoundNumber = (game.currentRoundNumber + 1) % game.roundsNum
                    viewModel.game.value?.currentRound = game.allDraws[game.currentRoundNumber]
                }
            }
        }
        updateVisually()
    }

    private fun updateVisually() {
        binding.roundNumberShow.text = "${(viewModel.game.value!!.currentRoundNumber + 1)}"
        binding.originalWordShow.text = viewModel.game.value?.getOriginal()
        binding.guessedWordShow.text = viewModel.game.value?.getGuess()
    }
}