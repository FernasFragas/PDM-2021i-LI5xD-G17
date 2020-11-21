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
        //viewModel.initiatePlayerDragDraw()
        binding.dragDrawView.viewModel = viewModel
        binding.userInput.visibility = View.INVISIBLE
        binding.hint.visibility = View.VISIBLE
        binding.hint.text = viewModel.getOriginalWord()
        /*
        binding.hint.text = viewModel.game.value?.currentWord
        viewModel.addOriginal(viewModel.game.value!!.currentWord)   //TODO outra coisa

         */
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

    private fun setSubmitListener() {
        binding.submitButton.setOnClickListener {
            changeState()
        }
    }

    private fun changeState() {
        var model = viewModel.game.value!!

        //TODO: ver se é preciso
        if (model.state == State.GUESSING)
            viewModel.addGuess(binding.userInput.editText?.text.toString())
        /*
        else {
            binding.userInput.editText?.setText("")
            viewModel.game.value?.currentWord = ""
        }
        */


        viewModel.changeState()

        if(model.round == State.NEW_ROUND) {
            binding.gameOver?.visibility = View.VISIBLE
            binding.gameOver?.text = "Round " + model.currentRoundNumber
        }
        runDelayed(10000) {

            binding.gameOver?.visibility = View.INVISIBLE
        }

        if (model.state == State.FINISHED) {
            binding.submitButton.isEnabled = false
            binding.gameOver?.visibility = View.VISIBLE
            binding.gameOver?.text = this.applicationContext.getText(R.string.Over)
            val intent = Intent(this, ShowActivity::class.java)
            intent.putExtra(Keys.GAME_KEY.name, model)
            startActivity(intent)
        }
        //viewModel.game.value?.timer = -1 //TODO TIRAR O NUMERO MAGICO
    }
}