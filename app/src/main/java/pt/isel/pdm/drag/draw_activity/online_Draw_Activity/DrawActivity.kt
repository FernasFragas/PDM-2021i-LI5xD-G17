package pt.isel.pdm.drag.draw_activity.online_Draw_Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent.*
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.drag.utils.Keys
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.draw_activity.DragViewModel
import pt.isel.pdm.drag.draw_activity.LocalDragViewModel
import pt.isel.pdm.drag.draw_activity.model.Position
import pt.isel.pdm.drag.draw_activity.model.State
import pt.isel.pdm.drag.list_games_activity.ListGamesActivity
import pt.isel.pdm.drag.showActivity.ShowActivity
import pt.isel.pdm.drag.utils.ChallengeInfo

/**
 * Key to be used when adding the [Player] instance that represents the local player
 */
const val LOCAL_PLAYER_EXTRA_ID = "GameActivity.LocalPlayerListenExtraID"

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

    private var gameMode: Boolean = false

/*
    private val localPlayer: MyOnlineID by lazy {
        intent.getParcelableExtra<MyOnlineID>(LOCAL_PLAYER_EXTRA_ID) ?:
        throw IllegalArgumentException("Mandatory extra $LOCAL_PLAYER_EXTRA_ID not present")
    }
*/
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

    val isOnline: Boolean by lazy {
        challenge.playerNum != (-1).toLong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //O PRIMEIRO ACESSO, Á DELEGATED PROPRIETIE É FEITO AQUI
/*
        if (!isOnline) {
            val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
            val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)

            viewModel.startGame(challenge.playerCapacity.toInt(), challenge.roundNum.toInt(),false)
            viewModel.addOriginalWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())

            viewModel.gameRepo.myState = State.NEW_ROUND.ordinal
        }*/
        /*
        val playerCount = intent.getIntExtra(Keys.PLAYER_COUNT_KEY.name, 0)
        val roundCount = intent.getIntExtra(Keys.ROUND_COUNT_KEY.name, 0)

        viewModel.startGame(challenge.playerCapacity.toInt(), challenge.roundNum.toInt(),gameMode)
        viewModel.addOriginalWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())

         */

        gameMode = intent.getBooleanExtra(Keys.GAME_MODE.name, false)

        if (gameMode) {
            viewModel.setGameMode(intent.getBooleanExtra(Keys.GAME_MODE.name, false))    //indico q o jogo é online
            viewModel.gameRepo.localID = intent.getIntExtra(LOCAL_PLAYER_EXTRA_ID, 0)
        }else {
            localViewModel.startGame(challenge.playerCapacity.toInt(), challenge.roundNum.toInt())
            localViewModel.addOriginalWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())
        }

        prepareListeners()

        if (viewModel.game.value?.playersNum==0) {
            viewModel.gameRepo.myState = State.WAITING.ordinal
        }

        if(gameMode) {
            viewModel.game.observe(this) {
                binding.dragDrawView.viewModel = viewModel
                viewModel.gameRepo.myState = viewModel.game.value?.state!!.ordinal
                when (viewModel.gameRepo.myState) {
                    State.DRAWING.ordinal -> drawOnGoing()
                    State.GUESSING.ordinal -> guessState()
                    State.WAITING.ordinal -> waitingForPlayersState()
                    State.FINISH_SCREEN.ordinal -> finishState()
                    State.CHANGE_ACTIVITY.ordinal -> changeActivity()
                    State.NEW_ROUND.ordinal -> newRoundState()
                }
            }
        }
        else {
            localViewModel.game.observe(this) {
                when (localViewModel.game.value?.state) {
                    State.DRAWING -> drawOnGoing()
                    State.GUESSING -> guessState()
                    State.FINISH_SCREEN -> finishState()
                    State.NEW_ROUND -> newRoundState()
                    State.CHANGE_ACTIVITY -> changeActivity()
                    State.WAITING -> waitingForPlayersState()
                }
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
        intent.putExtra(Keys.CHALLENGE_INFO.name, challenge)
        intent.putExtra(Keys.GAME_MODE.name, true)
        startActivity(intent)
    }

    /**
     *  adequa a activity á opção de quando é para desenhar
     */
    private fun drawOnGoing() {
        if (gameMode) {
            if (challenge.playerCapacity.toInt() == viewModel.game.value?.playersNum) {
                if (viewModel.gameRepo.localID == viewModel.game.value?.currentID) {  //quando é a vez do jogador jogar
                    closeWaitingMessage()
                    binding.dragDrawView.visibility = View.VISIBLE
                    binding.divider2.visibility = View.VISIBLE
                    binding.dragDrawView.viewModel = viewModel
                    binding.userInput.visibility = View.GONE
                    binding.hint.visibility = View.VISIBLE
                    binding.gameOver?.visibility = View.GONE
                    binding.submitButton.visibility = View.VISIBLE
                    binding.hint.text = viewModel.getOriginalWord()
                    binding.userInput.editText?.setText("")
                    setDrawListener()
                } else {        //quando é outro que não ele a jogar
                    binding.dragDrawView.visibility = View.INVISIBLE
                    writeMessage("Waiting for Others Players 2")
                }
                return
            }
        } else {

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
    }

    /**
     * adequa a activity á opção de quando é para adivinhar
     */
    private fun guessState() {
        if (gameMode) {
            if (challenge.playerCapacity.toInt() == viewModel.game.value?.playersNum) {
                if (viewModel.gameRepo.localID == viewModel.game.value?.currentID) {  //quando é a vez do jogador jogar
                    closeWaitingMessage()
                    binding.dragDrawView.visibility = View.VISIBLE
                    binding.divider2.visibility = View.VISIBLE
                    binding.userInput.visibility = View.VISIBLE
                    binding.hint.visibility = View.GONE
                    binding.gameOver?.visibility = View.GONE
                    binding.submitButton.visibility = View.VISIBLE
                } else { //quando é outro que não ele a jogar
                    binding.dragDrawView.visibility = View.INVISIBLE
                    writeMessage("Waiting for Others Players 2")
                }
                return
            }
        } else {

            binding.divider2.visibility = View.VISIBLE
            binding.userInput.visibility = View.VISIBLE
            binding.hint.visibility = View.GONE
            binding.gameOver?.visibility = View.GONE
            binding.submitButton.visibility = View.VISIBLE
        }
    }


    /**
     * função para escrever uma mensagem no meio do ecrã do jogo
     */
    private fun writeMessage(message: String) {
        binding.divider2.visibility = View.GONE
        binding.userInput.visibility = View.GONE
        binding.hint.visibility = View.GONE
        binding.gameOver?.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
        binding.gameOver?.text = message
    }

    /**
     * função para retirar mensagem do meio do ecrã do jogo
     */
    private fun closeWaitingMessage() {
        binding.divider2.visibility = View.VISIBLE
        binding.userInput.visibility = View.VISIBLE
        binding.hint.visibility = View.VISIBLE
        binding.gameOver?.visibility =  View.GONE
        binding.submitButton.visibility = View.VISIBLE
    }

    /**
     * logica do estado newState
     */
    private fun newRoundState() {
        binding.forfeitButton!!.visibility = View.GONE
        writeMessage("Round " + viewModel.game.value?.currentRoundNumber)
        if (gameMode) {
            viewModel.setTimer()
            viewModel.changeState()
        } else {
            localViewModel.changeState()
        }
    }

    // estado em que se está à espera dos adversários
    private fun waitingForPlayersState() {
        writeMessage("Waiting for Others Players 1")
        if(gameMode) {
            if (challenge.playerCapacity == challenge.playerNum && viewModel.game.value?.playersNum == 0) {  //verifica se já tem os jogadores necessarios para jogar
                viewModel.startGame(challenge.playerCapacity.toInt(), challenge.roundNum.toInt(), gameMode)
                viewModel.initialWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())
                viewModel.updateCloudGame()
            }
        } else {
            localViewModel.startGame(challenge.playerCapacity.toInt(), challenge.roundNum.toInt())
            localViewModel.initialWord(intent.getStringExtra(Keys.GAME_WORD_KEY.name).toString())
        }

    }

    private fun finishState() {
        binding.divider2.visibility = View.GONE
        binding.userInput.visibility = View.GONE
        binding.hint.visibility = View.GONE
        binding.submitButton.visibility = View.GONE
        binding.gameOver?.visibility = View.VISIBLE
        binding.gameOver?.text = this.applicationContext.getText(R.string.Over)
        viewModel.changeState()
    }

    /**
     * Listeners
     */
    private fun prepareListeners() {
        //presentation()
        setDrawListener()
        setSubmitListener()
        setForfeitListener()
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
            if (gameMode) {
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
            } else {
                if (localViewModel.game.value?.state == State.DRAWING) {
                    val winWidth = binding.dragDrawView.width
                    val winHeight = binding.dragDrawView.height
                    when (event.action) {
                        ACTION_DOWN ->
                            localViewModel.initiatePlayerLine(Position(event.x / winWidth, event.y / winHeight))
                        ACTION_MOVE -> {
                            localViewModel.addPlayerLine(Position(event.x / winWidth, event.y / winHeight))
                            localViewModel.initiatePlayerLine(Position(event.x / winWidth, event.y / winHeight))
                        }
                        ACTION_UP -> localViewModel.addPlayerLine(Position(event.x / winWidth, event.y / winHeight))
                    }
                }
                true
            }
        }
    }

    /**
     *  muda o estado no model, envia o Guess para o modelo se o estado atual for Guessing
     */
    private fun setSubmitListener() {
        binding.submitButton.setOnClickListener {
            if (viewModel.game.value!!.state == State.GUESSING)
                viewModel.addGuess(binding.userInput.editText?.text.toString())
            if(gameMode) {
                viewModel.updateCloudGame()
            }
            viewModel.changeState()
        }
    }

    //todo() melhor
    private fun setForfeitListener() {
        binding.forfeitButton!!.setOnClickListener {
            challenge.playerNum = challenge.playerNum - 1
            if(gameMode) {
                viewModel.updateCloudGame()
            }
            startActivity(Intent(this, ListGamesActivity::class.java))
        }
    }
}