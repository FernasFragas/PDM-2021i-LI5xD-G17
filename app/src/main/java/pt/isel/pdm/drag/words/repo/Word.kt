package pt.isel.pdm.drag.words.repo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Word(
    val word: String,
) : Parcelable

/**
 * Maps a DTO instance to the corresponding model instance
 */
fun modelFromDTO(dto: WordDTO) = Word(
    dto.word,
)