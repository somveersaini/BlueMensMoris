package org.bluechat.blueninemenmoris.model

class RandomAIPlayer @Throws(GameException::class)
constructor(player: Token, numPiecesPerPlayer: Int) : AIPlayer(player, numPiecesPerPlayer) {

    override fun getIndexToPlacePiece(gameBoard: Board): Int {
        while (true) {
            val index = rand.nextInt(Board.NUM_POSITIONS_OF_BOARD)
            try {
                if (!gameBoard.getPosition(index).isOccupied) {
                    return index
                }
            } catch (e: GameException) {
                e.printStackTrace()
            }

        }
    }

    override fun getIndexToRemovePieceOfOpponent(gameBoard: Board): Int {
        while (true) {
            try {
                val index = rand.nextInt(Board.NUM_POSITIONS_OF_BOARD)
                val playerOccupying = gameBoard.getPosition(index).playerOccupyingIt
                if (playerOccupying != Token.NO_PLAYER && playerOccupying != this.playerToken) {
                    return index
                }
            } catch (e: GameException) {
                e.printStackTrace()
            }

        }
    }

    override fun getPieceMove(gameBoard: Board, gamePhase: Int): Move {
        while (true) {
            try {
                val srcIndex = rand.nextInt(Board.NUM_POSITIONS_OF_BOARD)
                val position = gameBoard.getPosition(srcIndex)
                if (position.playerOccupyingIt == this.playerToken) {
                    val adjacents = position.adjacentPositionsIndexes
                    for (i in adjacents!!.indices) {
                        val adjacentPos = gameBoard.getPosition(adjacents[i])

                        if (!adjacentPos.isOccupied) {
                            adjacentPos.setAsOccupied(playerToken)
                            position.setAsUnoccupied()

                            val move = Move(srcIndex, adjacents[i], -1, Move.MOVING)

                            for (p in 0 until Board.NUM_MILL_COMBINATIONS) { //check if piece made a mill
                                var playerPieces = 0
                                var selectedPiece = false
                                val row = gameBoard.getMillCombination(p)

                                for (j in 0 until Board.NUM_POSITIONS_IN_EACH_MILL) {

                                    if (row[j].playerOccupyingIt == playerToken) {
                                        playerPieces++
                                    }
                                    if (row[j].positionIndex == move.destIndex) {
                                        selectedPiece = true
                                    }
                                }
                                if (playerPieces == 3 && selectedPiece) { // made a mill - select piece to remove
                                    move.removePieceOnIndex = getIndexToRemovePieceOfOpponent(gameBoard)
                                    break
                                }
                            }
                            position.setAsOccupied(playerToken)
                            adjacentPos.setAsUnoccupied()
                            return move
                        }
                    }
                }
            } catch (e: GameException) {
                e.printStackTrace()
            }

        }
    }


}
