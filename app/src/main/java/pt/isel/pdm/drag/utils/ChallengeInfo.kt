package pt.isel.pdm.drag.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Data class with the information that will be added to the collection Challenges in the
 * Firestore
 *
 * @property [id]                   challenge identifier
 * @property [challengeName]        challenge name
 * @property [playersNumb]          number needed of players for the challenge begin
 */
@Parcelize
data class ChallengeInfo(val id: String, val challengeName: String, val playersNumb: Int): Parcelable