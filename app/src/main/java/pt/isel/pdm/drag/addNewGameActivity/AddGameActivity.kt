package pt.isel.pdm.drag.addNewGameActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_game_layout.*
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.databinding.ActivityAddGameLayoutBinding
import pt.isel.pdm.drag.utils.State

/**
 * The key used to identify the result extra bearing the [ChallengeInfo] instance
 */
const val RESULT_EXTRA = "CCA.Result"

/**
 * The activity used to create a new challenge.
 *
 * The result of the creation operation is made available through [setResult] and if the creation
 * was successful, the corresponding [ChallengeInfo] instance is placed as the extra [RESULT_EXTRA]
 * of the result [Intent].
 */

class AddGameActivity : AppCompatActivity() {

    /**
     * The associated view model instance
     */
    private val viewModel: AddGameViewModel by viewModels()

    private val binding: ActivityAddGameLayoutBinding by lazy {
        ActivityAddGameLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.result.observe(this) {
            if (it.state == State.COMPLETE) {
                if (it.result != null) {
                    setResult(Activity.RESULT_OK, Intent().putExtra(RESULT_EXTRA, it.result))
                    finish()
                } else {
                    Toast.makeText(this, R.string.error_creating_challenge, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.createButton.setOnClickListener {
            viewModel.createChallenge(
                    binding.name.text.toString(),
                    Integer.parseInt(binding.playerNumber.text.toString()),
                    Integer.parseInt(binding.roundNumber.text.toString())
            )
        }


    }
}