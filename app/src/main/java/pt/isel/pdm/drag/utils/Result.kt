package pt.isel.pdm.drag.utils

enum class State {}

data class Result<R,E>(
    val state: State
)
