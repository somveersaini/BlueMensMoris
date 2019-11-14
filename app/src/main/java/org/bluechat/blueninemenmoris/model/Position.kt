package org.bluechat.blueninemenmoris.model

/**
 * Created by Samsaini on 05/25/2016.
 */
class Position(val positionIndex: Int = 0) {

    var isOccupied: Boolean = false
        private set
    var playerOccupyingIt: Token? = null
        private set
    var posx: Int = 0
        private set
    var posy: Int = 0
        private set


    lateinit var adjacentPositionsIndexes: IntArray
        private set

    init {
        isOccupied = false
        playerOccupyingIt = Token.NO_PLAYER
    }

    fun setAsOccupied(player: Token) {
        isOccupied = true
        playerOccupyingIt = player
    }

    /**
     * Clears a position and returns the token of the player that was there
     * @return
     */
    fun setAsUnoccupied(): Token? {
        isOccupied = false
        val oldPlayer = playerOccupyingIt
        playerOccupyingIt = Token.NO_PLAYER
        return oldPlayer
    }

    fun addAdjacentPositionsIndexes(pos1: Int, pos2: Int) {
        adjacentPositionsIndexes = IntArray(2)
        adjacentPositionsIndexes[0] = pos1
        adjacentPositionsIndexes[1] = pos2
    }

    fun addAdjacentPositionsIndexes(pos1: Int, pos2: Int, pos3: Int) {
        adjacentPositionsIndexes = IntArray(3)
        adjacentPositionsIndexes[0] = pos1
        adjacentPositionsIndexes[1] = pos2
        adjacentPositionsIndexes[2] = pos3
    }

    fun addAdjacentPositionsIndexes(pos1: Int, pos2: Int, pos3: Int, pos4: Int) {
        adjacentPositionsIndexes = IntArray(4)
        adjacentPositionsIndexes[0] = pos1
        adjacentPositionsIndexes[1] = pos2
        adjacentPositionsIndexes[2] = pos3
        adjacentPositionsIndexes[3] = pos4
    }

    fun isAdjacentToThis(posIndex: Int): Boolean {
        for (i in adjacentPositionsIndexes.indices) {
            if (adjacentPositionsIndexes[i] == posIndex) {
                return true
            }
        }
        return false
    }

    fun setPosxy(posx: Int, posy: Int) {
        this.posx = posx
        this.posy = posy
    }

}
