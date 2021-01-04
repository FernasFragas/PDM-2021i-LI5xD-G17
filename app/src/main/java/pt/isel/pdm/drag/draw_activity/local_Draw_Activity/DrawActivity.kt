package pt.isel.pdm.drag.draw_activity.local_Draw_Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent.*
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.utils.Keys
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.DragViewModel
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.showActivity.ShowActivity

/**
 * Activity referent for drawing
 */
class DrawActivity : AppCompatActivity() {

    /**
     * DELEGATED PROPRIETIES
     * propriedades da classe, como só podem ser iniciadas depois da activity ser iniciada(depois do onCreate)
     * para tal usamos a forma lazy
     */
    private val binding: ActivityDrawBinding by lazy { ActivityDrawBinding.inflate(layoutInflater) }
    private val viewModel: DragViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //O PRIMEIRO ACESSO, Á DELEGATED PROPRIETIE É FEITO AQUI

        val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
        val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)

        viewModel.startGame(playerCount, roundCount)
        viewModel.initialWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())

        prepareListeners()

        viewModel.game.observe(this) {
            when (viewModel.game.value?.state) {
                State.DRAWING -> drawOnGoing()
                State.GUESSING -> guessState()
                State.FINISH_SCREEN -> finishState()
                State.NEW_ROUND -> newRoundState()
                State.CHANGE_ACTIVITY -> changeActivity()
            }
            /*
            if(viewModel.game.value?.round == State.NEW_ROUND)
                newRoundState()

             */

        }

        /*
        viewModel.time.observe(this){
            binding.counter?.text = viewModel.game.value?.timer.toString()
        }
        viewModel.changeTimer(1000)
        */
    }

    private fun changeActivity() {
        //viewModel.gameRepo.saveGame(viewModel.game.value!!)
        val intent = Intent(this, ShowActivity::class.java)
        intent.putExtra(Keys.GAME_KEY.name, viewModel.game.value!!)
        startActivity(intent)
    }

    /**
     *  adequa a activity á opção de quando é para desenhar
     */
    private fun drawOnGoing() {
        binding.divider2.visibility = View.VISIBLE
        binding.dragDrawView.viewModel = viewModel
        binding.userInput.visibility = View.GONE
        binding.hint.visibility = View.VISIBLE
        binding.gameOver?.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
        binding.hint.text = viewModel.getOriginalWord()
        binding.userInput.editText?.setText("")
        setDrawListener()
    }

    /**
     * adequa a activity á opção de quando é para adivinhar
     */
    private fun guessState() {
        binding.divider2.visibility = View.VISIBLE
        binding.userInput.visibility = View.VISIBLE
        binding.hint.visibility = View.GONE
        binding.gameOver?.visibility = View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }

    /**
     * logica do estado newState
     */
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

    /**
     * Listeners
     */
    private fun prepareListeners() {
        //presentation()
        setDrawListener()
        setSubmitListener()
    }

    /*
    private fun presentation() {
        binding.gameOver?.visibility = View.VISIBLE
        binding.gameOver?.text = "Round Number: " + viewModel.game.value!!.currentRoundNumber
        runDelayed(3000) {
            binding.gameOver?.visibility = View.INVISIBLE
        }
    }

     */

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

    /**
     *  muda o estado no model, envia o Guess para o modelo se o estado atual for Guessing
     */
    private fun setSubmitListener() {
        binding.submitButton.setOnClickListener {
            if (viewModel.game.value!!.state == State.GUESSING)
                viewModel.addGuess(binding.userInput.editText?.text.toString())
            viewModel.changeState()
        }
    }
}