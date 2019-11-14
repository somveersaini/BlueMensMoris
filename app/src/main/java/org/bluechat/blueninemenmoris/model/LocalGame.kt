package org.bluechat.blueninemenmoris.model

class LocalGame : Game() {
    var player1: Player? = null
        private set
    var player2: Player? = null
        private set
    var currentTurnPlayer: Player? = null

    val opponentPlayer: Player?
        get() = if (currentTurnPlayer == player1) player2 else player1

    fun setPlayers(p1: Player, p2: Player) {
        player1 = p1
        player2 = p2
        currentTurnPlayer = player1
    }

    override val player: Player?
     get() = currentTurnPlayer

    fun updateCurrentTurnPlayer() {
        if (currentTurnPlayer == player1) {
            currentTurnPlayer = player2
        } else {
            currentTurnPlayer = player1
        }
    }

    @Throws(GameException::class)
    override fun removePiece(boardIndex: Int, player: Token): Boolean {
        if (super.removePiece(boardIndex, player)) {
            val p = if (currentTurnPlayer == player1) player2 else player1
            p!!.lowerNumPiecesOnBoard()
            return true
        }
        return false
    }
}
