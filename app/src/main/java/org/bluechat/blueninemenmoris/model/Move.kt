package org.bluechat.blueninemenmoris.model

class Move @Throws(GameException::class)
constructor(var srcIndex: Int, var destIndex: Int, var removePieceOnIndex: Int, val typeOfMove: Int) {
    var score: Int = 0

    init {
        if (typeOfMove != PLACING && typeOfMove != MOVING && typeOfMove != REMOVING) {
            throw GameException(javaClass.name + " - Invalid Type Of Move")
        }
    }

    companion object {

        val PLACING = 1
        val MOVING = 2
        val REMOVING = 3
    }
}