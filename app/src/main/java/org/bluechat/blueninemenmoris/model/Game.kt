package org.bluechat.blueninemenmoris.model


import android.util.Log

open class Game {

    var gameBoard: Board
        protected set
    var currentGamePhase: Int = 0
        protected set

    open val player: Player?
        get() = null

    // check if each player has at least one valid move
    // must only change if boolean is false
    val isTheGameOver: Boolean
        get() {
            try {
                if (gameBoard.getNumberOfPiecesOfPlayer(Token.PLAYER_1) == Game.MIN_NUM_PIECES || gameBoard.getNumberOfPiecesOfPlayer(Token.PLAYER_2) == Game.MIN_NUM_PIECES) {
                    return true
                } else {
                    var p1HasValidMove = false
                    var p2HasValidMove = false
                    var player: Token?
                    for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
                        val position = gameBoard.getPosition(i)
                        player = position.playerOccupyingIt
                        if (player !== Token.NO_PLAYER) {
                            val adjacent = position.adjacentPositionsIndexes
                            for (j in adjacent.indices) {
                                val adjacentPos = gameBoard.getPosition(adjacent[j])
                                if (!adjacentPos.isOccupied) {
                                    if (!p1HasValidMove) {
                                        p1HasValidMove = player === Token.PLAYER_1
                                    }
                                    if (!p2HasValidMove) {
                                        p2HasValidMove = player === Token.PLAYER_2
                                    }
                                    break
                                }
                            }
                        }
                        if (p1HasValidMove && p2HasValidMove) {
                            return false
                        }
                    }
                }
            } catch (e: GameException) {
                e.printStackTrace()
                System.exit(-1)
            }

            return true
        }

    init {
        gameBoard = Board()
        currentGamePhase = Game.PLACING_PHASE
    }

    fun getPlayerInBoardPosition(boardPosition: Int): Token? {
        try {
            return gameBoard.getPosition(boardPosition).playerOccupyingIt
        } catch (e: GameException) {
            e.printStackTrace()
            System.exit(-1)
        }

        return Token.NO_PLAYER
    }

    @Throws(GameException::class)
    fun positionIsAvailable(boardIndex: Int): Boolean {
        return gameBoard.positionIsAvailable(boardIndex)
    }

    @Throws(GameException::class)
    fun validMove(currentPositionIndex: Int, nextPositionIndex: Int): Boolean {
        val currentPos = gameBoard.getPosition(currentPositionIndex)
        return if (currentPos.isAdjacentToThis(nextPositionIndex) && !gameBoard.getPosition(nextPositionIndex).isOccupied) {
            true
        } else false
    }

    @Throws(GameException::class)
    fun movePieceFromTo(srcIndex: Int, destIndex: Int, player: Token): Int {
        if (positionHasPieceOfPlayer(srcIndex, player)) {
            if (positionIsAvailable(destIndex)) {
                //System.out.println("Number of pieces: "+gameBoard.getNumberOfPiecesOfPlayer(player));
                if (validMove(srcIndex, destIndex) || gameBoard.getNumberOfPiecesOfPlayer(player) == Game.MIN_NUM_PIECES + 1) {
                    gameBoard.getPosition(srcIndex).setAsUnoccupied()
                    gameBoard.getPosition(destIndex).setAsOccupied(player)
                    return Game.VALID_MOVE
                } else {
                    return Game.INVALID_MOVE
                }
            } else {
                return Game.UNAVAILABLE_POS
            }
        } else {
            return Game.INVALID_SRC_POS
        }
    }

    @Throws(GameException::class)
    fun placePieceOfPlayer(boardPosIndex: Int, player: Token): Boolean {
        if (gameBoard.positionIsAvailable(boardPosIndex)) {
            gameBoard.getPosition(boardPosIndex).setAsOccupied(player)
            gameBoard.incNumPiecesOfPlayer(player)
            if (gameBoard.incNumTotalPiecesPlaced() == NUM_PIECES_PER_PLAYER * 2) {
                currentGamePhase = Game.MOVING_PHASE
            }
            return true
        }
        return false
    }

    @Throws(GameException::class)
    fun madeAMill(dest: Int, player: Token): Boolean {
        var maxNumPlayerPiecesInRow = 0
        for (i in 0 until Board.NUM_MILL_COMBINATIONS) {
            val row = gameBoard.getMillCombination(i)
            for (j in 0 until Board.NUM_POSITIONS_IN_EACH_MILL) {
                if (row[j].positionIndex == dest) {
                    val playerPiecesInThisRow = numPiecesFromPlayerInRow(row, player)
                    if (playerPiecesInThisRow > maxNumPlayerPiecesInRow) {
                        maxNumPlayerPiecesInRow = playerPiecesInThisRow
                    }
                }
            }
        }
        return maxNumPlayerPiecesInRow == Board.NUM_POSITIONS_IN_EACH_MILL
    }

    private fun numPiecesFromPlayerInRow(pos: Array<Position>, player: Token): Int {
        var counter = 0
        for (i in pos.indices) {
            if (pos[i].playerOccupyingIt === player) {
                counter++
            }
        }
        return counter
    }

    @Throws(GameException::class)
    fun positionHasPieceOfPlayer(boardIndex: Int, player: Token): Boolean {
        return gameBoard.getPosition(boardIndex).playerOccupyingIt === player
    }

    fun printGameBoard() {
        gameBoard.printBoard()
    }

    @Throws(GameException::class)
    open fun removePiece(boardIndex: Int, player: Token): Boolean {
        if (!gameBoard.positionIsAvailable(boardIndex) && positionHasPieceOfPlayer(boardIndex, player)) {
            gameBoard.getPosition(boardIndex).setAsUnoccupied()
            gameBoard.decNumPiecesOfPlayer(player)
            if (currentGamePhase == Game.MOVING_PHASE && gameBoard.getNumberOfPiecesOfPlayer(player) == Game.MIN_NUM_PIECES + 1) {
                currentGamePhase = Game.FLYING_PHASE
                Log.d("New game phase is:", " $currentGamePhase")
            }
            return true
        }
        return false
    }

    companion object {

        val NUM_PIECES_PER_PLAYER = 9
        val PLACING_PHASE = 1
        val MOVING_PHASE = 2
        val FLYING_PHASE = 3

        val INVALID_SRC_POS = -1
        val UNAVAILABLE_POS = -2
        val INVALID_MOVE = -3
        val VALID_MOVE = 0

        val MIN_NUM_PIECES = 2
    }
}
