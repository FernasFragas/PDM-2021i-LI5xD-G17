package pt.isel.pdm.drag.list_games_activity.view

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.drag.R
import pt.isel.pdm.drag.utils.ChallengeInfo


//TODO POR AS STRINGS BEM
/**
 * Represents views (actually, the corresponding holder) that display the information pertaining to
 * a [ChallengeInfo] instance
 */
class ChallengeViewHolder(private val view: ViewGroup) : RecyclerView.ViewHolder(view) {

    private val challengeNameView: TextView = view.findViewById(R.id.challengeName)
    private val roundNumberView: TextView = view.findViewById(R.id.rounds)
    private val playersInformationView: TextView = view.findViewById(R.id.currentPlayers)

    /**
     * Starts the item selection animation and calls [onAnimationEnd] once the animation ends
     */
    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(view.context, R.color.list_item_background),
            ContextCompat.getColor(view.context, R.color.list_item_background_selected),
            ContextCompat.getColor(view.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = view.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.start()

        animation.doOnEnd { onAnimationEnd() }
    }

    /**
     * Used to create an association between the current view holder instance and the given
     * data item
     *
     * @param   challenge               the challenge data item
     * @param   itemSelectedListener    the function to be called whenever the item is selected
     */
    fun bindTo(challenge: ChallengeInfo?, itemSelectedListener: (ChallengeInfo) -> Unit) {
        challengeNameView.text = challenge?.challengeName ?: ""
        roundNumberView.text = "Round Number: ${challenge?.roundNum.toString()}"
        playersInformationView.text = "${challenge?.playerNum}/${challenge?.playerCapacity}"

        if (challenge != null)
            view.setOnClickListener {
                startAnimation {
                    itemSelectedListener(challenge)
                }
            }
    }
}

/**
 * Adapts [ListGamesViewModel] instances to be displayed in a [RecyclerView]
 */
class ChallengesListAdapter(
    private val contents: List<ChallengeInfo>,
    private val itemSelectedListener: (ChallengeInfo) -> Unit) :
    RecyclerView.Adapter<ChallengeViewHolder>() {

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bindTo(contents[position], itemSelectedListener)
    }

    override fun getItemCount(): Int = contents.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false) as ViewGroup

        return ChallengeViewHolder(view)
    }
}