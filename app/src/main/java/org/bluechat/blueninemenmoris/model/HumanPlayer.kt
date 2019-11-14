package org.bluechat.blueninemenmoris.model

class HumanPlayer @Throws(GameException::class)
constructor(name: String, player: Token, numPiecesPerPlayer: Int) : Player(player, numPiecesPerPlayer) {

    init {
        this.name = name
    }

    override val isAI: Boolean
        get() = false
}
