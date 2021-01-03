package pt.isel.pdm.drag.utils

/**
 * represents the state of the connections to the cloud
 * IDLE - when the connection is not being used
 * ONGOING - when the connection is established and is communicating with the cloud service
 * COMPLETE - when the connection is finish, and is no longer communicating whit the cloud service
 */
enum class State {IDLE, ONGOING, COMPLETE}

/**
 * represents the result obtain from the cloud, in case the information was well retrieve we obtain
 * the info in the result field,
 * if there were an error we obtain the error information in the error field
 */
data class Result<R,E>(
    val state: State = State.IDLE,
    val result: R? = null,
    val error: E? = null
)
