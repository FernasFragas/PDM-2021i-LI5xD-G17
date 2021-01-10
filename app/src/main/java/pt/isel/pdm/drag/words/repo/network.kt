package pt.isel.pdm.drag.words.repo

import com.fasterxml.jackson.annotation.JsonProperty

class WordDTO(
    @JsonProperty("word") val word: String,
)