package pt.isel.pdm.drag.list_games_activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.addNewGameActivity.AddGameActivity
import pt.isel.pdm.drag.addNewGameActivity.RESULT_EXTRA
import pt.isel.pdm.drag.databinding.ActivityListGamesLayoutBinding
import pt.isel.pdm.drag.draw_activity.local_Draw_Activity.DrawActivity
import pt.isel.pdm.drag.draw_activity.local_Draw_Activity.LOCAL_PLAYER_EXTRA_ID
import pt.isel.pdm.drag.draw_activity.model.MyOnlineID
import pt.isel.pdm.drag.list_games_activity.view.ChallengesListAdapter
import pt.isel.pdm.drag.utils.ChallengeInfo
import pt.isel.pdm.drag.utils.Keys
import pt.isel.pdm.drag.utils.State


private const val CREATE_CODE = 10001

class ListGamesActivity : AppCompatActivity(){

    /**
     * updates the list displayed with the game in the fundraising stage
     */
    private fun updateChallengesList() {
        binding.refreshLayout.isRefreshing = true
        viewModel.fetchChallenges()
    }

    /**
     * Called whenever a list element is selected. The player that accepts the challenge is the
     * first to make a move.
     *
     * @param challenge the selected challenge
     */
    private fun challengeSelected(challenge: ChallengeInfo) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.accept_challenge_dialog_title, challenge.challengeName))
            .setPositiveButton(R.string.accept_challenge_dialog_ok) { _, _ -> viewModel.tryAcceptChallenge(challenge) }
            .setNegativeButton(R.string.accept_challenge_dialog_cancel, null)
            .create()
            .show()
    }


    private val binding: ActivityListGamesLayoutBinding by lazy {
        ActivityListGamesLayoutBinding.inflate(layoutInflater)
    }

    private val viewModel: ListGamesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.challengesList.setHasFixedSize(true)    //adapter changes cannot affect the size of the RecyclerView
        binding.challengesList.layoutManager = LinearLayoutManager(this)

        // Get view model instance and add its contents to the recycler view
        viewModel.challenges.observe(this) {
            binding.challengesList.adapter = ChallengesListAdapter(it, ::challengeSelected)
            binding.refreshLayout.isRefreshing = false
        }

        // Setup ui event handlers
        binding.refreshLayout.setOnRefreshListener {
            updateChallengesList()
        }

        //make the "connection" between the ListGamesActivity and the addGameActivity
        binding.createChallengeButton.setOnClickListener {
            startActivityForResult(Intent(this, AddGameActivity::class.java),
                    CREATE_CODE
            )
        }

        viewModel.enrolmentResult.observe(this) {

            if (it.state == State.COMPLETE) {
                if (it.result != null) {
                    startActivity(Intent(this, DrawActivity::class.java).apply {
                        putExtra(Keys.CHALLENGE_INFO.name, it.result)
                        putExtra(Keys.GAME_WORD_KEY.name, "ONLINE")
                        putExtra(Keys.GAME_MODE.name, true)
                        //supostamente deve dar o ID novo ao jogador, por exemplo se for o segundo jogador a entrar o ID dele é 1 (começa no 0)
                        putExtra(LOCAL_PLAYER_EXTRA_ID, it.result.playerNum.toInt()-1)
                    })
                } else {
                    Toast.makeText(this, R.string.error_accepting_challenge, Toast.LENGTH_LONG).show()
                }
                viewModel.resetEnrolmentResult()
                binding.challengesList.isEnabled = true
            } else {
                binding.challengesList.isEnabled = false
            }
        }
    }



    /**
     * Callback method that handles menu creation
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_challenges_list, menu)
        return true
    }

    /**
     * Callback method that handles menu item selection
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.challenges_list_update -> {
            updateChallengesList()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Callback method that handles the result obtained from activities launched to collect user
     * input.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CREATE_CODE -> if (resultCode == Activity.RESULT_OK) {
                updateChallengesList()
                val createdChallenge = data?.getParcelableExtra<ChallengeInfo>(RESULT_EXTRA)
                startActivity(Intent(this, DrawActivity::class.java).apply {
                    putExtra(Keys.CHALLENGE_INFO.name, createdChallenge)
                    putExtra(Keys.GAME_WORD_KEY.name, "ONLINE")
                    putExtra(Keys.GAME_MODE.name, true)
                    putExtra(LOCAL_PLAYER_EXTRA_ID, 0)
                })

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onStart() {
        super.onStart()
        updateChallengesList()
    }
}