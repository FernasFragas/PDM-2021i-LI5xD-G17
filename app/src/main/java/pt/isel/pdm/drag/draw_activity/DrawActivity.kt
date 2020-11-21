package pt.isel.pdm.drag.draw_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent.*
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.Keys
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.showActivity.ShowActivity
import pt.isel.pdm.drag.utils.runDelayed

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
                State.NEW_ROUND -> newRoundState()
                State.FINISHED -> finishState()
            }
        }

        /*
        viewModel.time.observe(this){
            binding.counter?.text = viewModel.game.value?.timer.toString()
        }
        viewModel.changeTimer(1000)
        */
    }

    /**
     *  adequa a activity á opção de quando é para desenhar
     */
    private fun drawOnGoing() {
        binding.dragDrawView.viewModel = viewModel
        binding.userInput.visibility = View.INVISIBLE
        binding.hint.visibility = View.VISIBLE
        binding.hint.text = viewModel.getOriginalWord()
        binding.userInput.editText?.setText("")
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
     * logica do estado newState
     */
    private fun newRoundState() {
        var model = viewModel.game.value!!
        if(model.round == State.NEW_ROUND) {
            binding.gameOver?.visibility = View.VISIBLE
            binding.gameOver?.text = "Round " + model.currentRoundNumber
        }
    }

    private fun finishState() {
        binding.submitButton.isEnabled = false
        binding.gameOver?.visibility = View.VISIBLE
        binding.gameOver?.text = this.applicationContext.getText(R.string.Over)
        val intent = Intent(this, ShowActivity::class.java)
        intent.putExtra(Keys.GAME_KEY.name, viewModel.game.value!!)
        startActivity(intent)
    }

    /**
     * Listeners
     */
    private fun prepareListeners() {
        presentation()
        setDrawListener()
        setSubmitListener()
    }

    private fun presentation() {
        binding.gameOver?.visibility = View.VISIBLE
        binding.gameOver?.text = "Round " +  viewModel.game.value!!.currentRoundNumber
        runDelayed(3000) {
            binding.gameOver?.visibility = View.INVISIBLE
        }
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

    //metodo desnecessário
    private fun changeState() {

        //TODO: não sei a que metodo isto pertence dps mete no metodo certo (newRoundState() ou finishState())
        runDelayed(10000) {
            binding.gameOver?.visibility = View.INVISIBLE
        }

        //viewModel.game.value?.timer = -1 //TODO TIRAR O NUMERO MAGICO
    }
}