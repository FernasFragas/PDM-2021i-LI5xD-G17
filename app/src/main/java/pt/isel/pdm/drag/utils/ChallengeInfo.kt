package pt.isel.pdm.drag.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Data class with the information that will be added to the collection Challenges in the
 * Firestore
 *
 * @property [id]                   challenge identifier
 * @property [challengeName]        challenge name
 * @property [playerCapacity]       max number of players for this challenge
 * @property playerNum              amount of players in this challenge
 * @property roundNum               number of rounds for this challenge
 */
@Parcelize
data class ChallengeInfo(
        val id: String,
        val challengeName: String,
        val playerCapacity: Long,
        var playerNum: Long,
        val roundNum: Long
): Parcelable