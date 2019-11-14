package org.bluechat.blueninemenmoris.model


abstract class Player protected constructor() {

    var name: String = ""
    protected var gamesWon: Int = 0
    var numPieces: Int = 0
        protected set
    var numPiecesOnBoard: Int = 0
        protected set
    lateinit var playerToken: Token
        protected set
    protected var canFly: Boolean = false
    lateinit var actors: Array<Actor?>
        protected set

    val numPiecesLeftToPlace: Int
        get() = numPieces - numPiecesOnBoard

    abstract val isAI: Boolean

    init {
        gamesWon = 0
        numPiecesOnBoard = 0
        canFly = false
    }

    @Throws(GameException::class)
    protected constructor(player: Token, numPiecesPerPlayer: Int) : this() {
        if (player != Token.PLAYER_1 && player != Token.PLAYER_2) {
            throw GameException("" + javaClass.name + " - Invalid Player Token: " + player)
        } else {
            numPieces = numPiecesPerPlayer
            playerToken = player
            actors = arrayOfNulls(numPieces)
        }
    }

    fun reset() {
        numPiecesOnBoard = 0
        canFly = false
    }

    fun raiseNumPiecesOnBoard(): Int {
        canFly = false // it's still placing pieces
        return ++numPiecesOnBoard
    }

    fun lowerNumPiecesOnBoard(): Int {
        if (--numPiecesOnBoard == 3) {
            canFly = true
        }
        return numPiecesOnBoard
    }

    fun canItFly(): Boolean {
        return canFly
    }

    fun setActors(sx: Int, sy: Int) {
        for (i in 0 until numPieces) {
            actors[i] = Actor((i + 1) * sx, sy, i + 1)
        }
    }

    fun getActorAt(index: Int): Actor? {
        var act: Actor? = null
        for (actor in actors) {
            if (actor?.placedIndex == index) {
                act = actor
            }
        }
        return act
    }

    fun getActorByNumber(index: Int): Actor? {
        var act: Actor? = null
        for (actor in actors) {
            if (actor?.number == index) {
                act = actor
            }
        }
        return act
    }
}
