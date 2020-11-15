package pt.isel.pdm.drag.model

/**
 * Contains all the information about the game
 * @param numOfUsers the number of users playing the game
 * @param numOfRounds the number of rounds of the game
 * @param playersContainer the container of the objects Players
 */

data class GameData(val numOfUsers: Int, val numOfRounds: Int, val playersContainer: HashMap<Int,Player>) {


}