//package org.bluechat.blueninemenmoris.model
//
//import java.util.ArrayList
//
//
//class NetworkGame : Game() {
//
//    lateinit var Nplayer: Player
//    var opponent: Player? = null
//    var isThisPlayerTurn: Boolean = false
//        protected set
//
//    init {
//        isThisPlayerTurn = false
//    }
//
//    fun setPlayer(p: Player) {
//        Nplayer = p
//    }
//
//    fun setTurn(turn: Boolean) {
//        isThisPlayerTurn = turn
//    }
//
//    fun playedFirst(playerThatPlayedFirst: Token): Boolean {
//        return Nplayer.playerToken == playerThatPlayedFirst
//    }
//
//    override val player: Player?
//        get() {
//            return Nplayer
//        }
//
//    @Throws(GameException::class)
//    override fun removePiece(boardIndex: Int, player: Token): Boolean {
//        if (super.removePiece(boardIndex, player)) {
//            if (player == this.Nplayer.playerToken) {
//                this.Nplayer.lowerNumPiecesOnBoard()
//            }
//            return true
//        }
//        return false
//    }
//
//    @Throws(GameException::class)
//    fun updateGameWithOpponentMoves(opponentMoves: ArrayList<Move>) {
//        for (move in opponentMoves) {
//            if (move.typeOfMove == Move.PLACING) {
//                placePieceOfPlayer(move.destIndex, if (player.playerToken == Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1)
//            } else if (move.typeOfMove == Move.REMOVING) {
//                removePiece(move.removePieceOnIndex, if (player.playerToken == Token.PLAYER_1) Token.PLAYER_1 else Token.PLAYER_2)
//            } else if (move.typeOfMove == Move.MOVING) {
//                movePieceFromTo(move.srcIndex, move.destIndex, if (player.playerToken == Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1)
//            }
//        }
//        opponentMoves.clear()
//    }
//}
