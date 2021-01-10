package pt.isel.pdm.drag.showActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_show.view.*
import pt.isel.pdm.drag.utils.Keys
import pt.isel.pdm.drag.databinding.ActivityShowBinding
import pt.isel.pdm.drag.draw_activity.DragViewModel
import pt.isel.pdm.drag.draw_activity.LocalDragViewModel
import pt.isel.pdm.drag.draw_activity.model.DragGame
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.utils.ChallengeInfo

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

    private val viewModel: DragViewModel by viewModels {
        @Suppress("UNCHECKED_CAST")
        object: ViewModelProvider.Factory {
            override fun <VM : ViewModel?> create(modelClass: Class<VM>): VM {
                return DragViewModel(application, challenge) as VM
            }
        }
    }

    private val localViewModel: LocalDragViewModel by viewModels()

    private val challenge: ChallengeInfo by lazy {
        intent.getParcelableExtra<ChallengeInfo>(Keys.CHALLENGE_INFO.name) ?:
        throw IllegalArgumentException("Mandatory extra ${Keys.CHALLENGE_INFO.name} not present")
    }

    private  var isOnline: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isOnline = intent.getBooleanExtra(Keys.GAME_MODE.name,false)


        if(isOnline)
            binding.showDraw.viewModel = viewModel
        else
            binding.showDraw.localViewModel = localViewModel

        if(isOnline) {
            val model = intent.getParcelableExtra<DragGame>(Keys.GAME_KEY.name)
            viewModel.game.value?.allDraws = model!!.allDraws
            viewModel.game.value?.currentRound = model.allDraws[model.currentRoundNumber - 1]
            viewModel.game.value?.playersNum = model.playersNum
            viewModel.game.value?.roundsNum = model.roundsNum
        }
        else {
            val model = intent.getParcelableExtra<DragGame>(Keys.GAME_KEY.name)
            localViewModel.game.value?.allDraws = model!!.allDraws
            localViewModel.game.value?.currentRound = model.allDraws[model.currentRoundNumber - 1]
            localViewModel.game.value?.playersNum = model.playersNum
            localViewModel.game.value?.roundsNum = model.roundsNum
        }
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


    //TODO PROBLEMA DE LOGICA
    private fun doSwipe(swipeState: SwipeState) {
        val game: DragGame
        if (isOnline) {
             game = viewModel.game.value!!
        }
        else {
            game =  localViewModel.game.value!!
        }

        when(swipeState) {
            SwipeState.RIGHT -> {
                if (game.currentID != 0) {
                    game.currentID = game.currentID - 1
                    updateVisually()
                } else {
                    game.currentRoundNumber = (game.currentRoundNumber - 1) % game.roundsNum
                    if (isOnline && game.currentRoundNumber >= 0) {
                        viewModel.game.value?.currentRound = game.allDraws[game.currentRoundNumber]
                    } else if (game.currentRoundNumber >= 0) {
                        localViewModel.game.value?.currentRound = game.allDraws[game.currentRoundNumber]
                    }
                }
            }
            SwipeState.LEFT -> {
                if (game.currentID != game.playersNum - 1) {
                    game.currentID = game.currentID + 1
                    updateVisually()
                }
                else {
                    game.currentRoundNumber = (game.currentRoundNumber + 1) % game.roundsNum
                    if (isOnline) {
                        viewModel.game.value?.currentRound = game.allDraws[game.currentRoundNumber]
                    } else {
                        localViewModel.game.value?.currentRound = game.allDraws[game.currentRoundNumber]
                    }
                }
            }
        }
        updateVisually()
    }

    private fun updateVisually() {
        if (isOnline) {
            binding.roundNumberShow.text = "${(viewModel.game.value!!.currentRoundNumber + 1)}"
            binding.originalWordShow.text = viewModel.game.value?.getOriginal()
            binding.guessedWordShow.text = viewModel.game.value?.getGuess()
        } else {
            binding.roundNumberShow.text = "${(localViewModel.game.value!!.currentRoundNumber + 1)}"
            binding.originalWordShow.text = localViewModel.game.value?.getOriginal()
            binding.guessedWordShow.text = localViewModel.game.value?.getGuess()
        }
    }
}