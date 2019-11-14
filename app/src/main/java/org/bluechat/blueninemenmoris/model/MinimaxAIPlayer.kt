package org.bluechat.blueninemenmoris.model

import android.util.Log

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator


class MinimaxAIPlayer @Throws(GameException::class)
constructor(player: Token, numPiecesPerPlayer: Int, private val depth: Int) : AIPlayer(player, numPiecesPerPlayer) {
    var bestScore = 0
    private val opponentPlayer: Token
    private var currentBestMove: Move? = null

    init {
        if (depth < 1) {
            throw GameException("" + javaClass.name + " - Invalid Minimax Player Depth")
        }
        opponentPlayer = if (player == Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
    }

    @Throws(GameException::class)
    private fun applyMove(move: Move, player: Token, gameBoard: Board, gamePhase: Int) {

        // Try this move for the current player
        val position = gameBoard.getPosition(move.destIndex)
        position.setAsOccupied(player)

        if (gamePhase == Game.PLACING_PHASE) {
            gameBoard.incNumPiecesOfPlayer(player)
        } else {
            gameBoard.getPosition(move.srcIndex).setAsUnoccupied()
        }

        if (move.removePieceOnIndex != -1) { // this move removed a piece from opponent
            val removed = gameBoard.getPosition(move.removePieceOnIndex)
            removed.setAsUnoccupied()
            gameBoard.decNumPiecesOfPlayer(getOpponentToken(player))
        }
    }

    @Throws(GameException::class)
    private fun undoMove(move: Move, player: Token, gameBoard: Board, gamePhase: Int) {
        // Undo move
        val position = gameBoard.getPosition(move.destIndex)
        position.setAsUnoccupied()

        if (gamePhase == Game.PLACING_PHASE) {
            gameBoard.decNumPiecesOfPlayer(player)
        } else {
            gameBoard.getPosition(move.srcIndex).setAsOccupied(player)
        }

        if (move.removePieceOnIndex != -1) {
            val opp = getOpponentToken(player)
            gameBoard.getPosition(move.removePieceOnIndex).setAsOccupied(opp)
            gameBoard.incNumPiecesOfPlayer(opp)
        }
    }

    private fun getOpponentToken(player: Token): Token {
        return if (player == playerToken) {
            opponentPlayer
        } else {
            playerToken
        }
    }

    override fun getIndexToPlacePiece(gameBoard: Board): Int {
        numberOfMoves = 0 // TODO TESTING
        movesThatRemove = 0 // TODO TESTING

        try {
            val moves = generateMoves(gameBoard, playerToken, Game.PLACING_PHASE) // sorted already

            for (move in moves) {
                applyMove(move, playerToken, gameBoard, Game.PLACING_PHASE)
                move.score = move.score + alphaBeta(opponentPlayer, gameBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE)
                undoMove(move, playerToken, gameBoard, Game.PLACING_PHASE)
            }

            Collections.sort(moves, HeuristicComparatorMax())

            // if there are different moves with the same score it returns one of them randomly
            val bestMoves = ArrayList<Move>()
            val bestScore = moves[0].score
            bestMoves.add(moves[0])
            for (i in 1 until moves.size) {
                if (moves[i].score == bestScore) {
                    bestMoves.add(moves[i])
                } else {
                    break
                }
            }
            currentBestMove = bestMoves[rand.nextInt(bestMoves.size)]
            return currentBestMove!!.destIndex
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(-1)
        }

        Log.d("minimaxaiplayer", "Should not get here")
        return -1
    }

    override fun getIndexToRemovePieceOfOpponent(gameBoard: Board): Int {
        return currentBestMove!!.removePieceOnIndex
    }

    @Throws(GameException::class)
    override fun getPieceMove(gameBoard: Board, gamePhase: Int): Move? {
        numberOfMoves = 0 // TODO TESTING
        movesThatRemove = 0 // TODO TESTING

        try {

            val moves = generateMoves(gameBoard, playerToken, getGamePhase(gameBoard, playerToken)) // sorted already
            if (moves.isEmpty()) {
                return null
            }
            for (move in moves) {
                applyMove(move, playerToken, gameBoard, Game.MOVING_PHASE)
                move.score = move.score + alphaBeta(opponentPlayer, gameBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE)
                undoMove(move, playerToken, gameBoard, Game.MOVING_PHASE)
            }

            Collections.sort(moves, HeuristicComparatorMax())

            // if there are different moves with the same score it returns one of them randomly
            val bestMoves = ArrayList<Move>()
            val bestScore = moves[0].score
            bestMoves.add(moves[0])
            for (i in 1 until moves.size) {
                if (moves[i].score == bestScore) {
                    bestMoves.add(moves[i])
                } else {
                    break
                }
            }
            currentBestMove = bestMoves[rand.nextInt(bestMoves.size)]
            return currentBestMove
        } catch (e: GameException) {
            e.printStackTrace()
            System.exit(-1)
        }

        Log.d("minimaxaiplayer", "Should not get here")
        return null
    }

    private fun alphaBeta(player: Token, gameBoard: Board, depth: Int, alpha: Int, beta: Int): Int {
        var alpha = alpha
        var beta = beta

        val gamePhase = getGamePhase(gameBoard, player)
        val gameOver: Int  = checkGameOver(gameBoard)
        val childMoves: List<Move>  = generateMoves(gameBoard, player, gamePhase)

        try {


            if (depth == 0) { // depth reached, evaluate score
                return evaluate(gameBoard, gamePhase)
            } else if ((gameOver) != 0) { // gameover
                return gameOver
            } else if ((childMoves).isEmpty()) {
                return if (player == playerToken) { // IT SHOULD RETURN DIFFERENT VALUES RIGHT? IF THE BOT DOESN'T HAVE ANY POSSIBLE MOVES, THEN THE PLAYER WINS, AND RETURNS MAX VALUE???
                    -maxScore
                } else {
                    maxScore
                }
            } else {

                for (move in childMoves) {

                    applyMove(move, player, gameBoard, gamePhase)

                    if (player == playerToken) {  // maximizing player
                        alpha = Math.max(alpha, alphaBeta(opponentPlayer, gameBoard, depth - 1, alpha, beta))

                        if (beta <= alpha) {
                            undoMove(move, player, gameBoard, gamePhase)
                            break // cutoff
                        }
                    } else {  //  minimizing player
                        beta = Math.min(beta, alphaBeta(playerToken, gameBoard, depth - 1, alpha, beta))
                        if (beta <= alpha) {
                            undoMove(move, player, gameBoard, gamePhase)
                            break // cutoff
                        }
                    }
                    undoMove(move, player, gameBoard, gamePhase)
                }

                return if (player == playerToken) {
                    alpha
                } else {
                    beta
                }
            }
        } catch (e: GameException) {
            e.printStackTrace()
            System.exit(-1)
        }

        Log.d("minimaxaiplayer", "SHOULD NOT GET HERE!")
        return -1
    }

    @Throws(GameException::class)
    private fun evaluate(gameBoard: Board, gamePhase: Int): Int {
        var score = 0
        var R1_numPlayerMills = 0
        var R1_numOppMills = 0
        var R2_numPlayerTwoPieceConf = 0
        var R2_numOppTwoPieceConf = 0

        for (i in 0 until Board.NUM_MILL_COMBINATIONS) {
            var playerPieces = 0
            var emptyCells = 0
            var opponentPieces = 0

            try {
                val row = gameBoard.getMillCombination(i)
                for (j in 0 until Board.NUM_POSITIONS_IN_EACH_MILL) {
                    if (row[j].playerOccupyingIt == playerToken) {
                        playerPieces++
                    } else if (row[j].playerOccupyingIt == Token.NO_PLAYER) {
                        emptyCells++
                    } else {
                        opponentPieces++
                    }
                }
            } catch (e: GameException) {
                e.printStackTrace()
            }

            if (playerPieces == 3) {
                R1_numPlayerMills++
            } else if (playerPieces == 2 && emptyCells == 1) {
                R2_numPlayerTwoPieceConf++
            } else if (playerPieces == 1 && emptyCells == 2) {
                score += 1
            } else if (opponentPieces == 3) {
                R1_numOppMills++
            } else if (opponentPieces == 2 && emptyCells == 1) {
                R2_numOppTwoPieceConf++
            } else if (opponentPieces == 1 && emptyCells == 2) {
                score += -1
            }

            val playerInPos = gameBoard.getPosition(i).playerOccupyingIt
            if (i == 4 || i == 10 || i == 13 || i == 19) {
                if (playerInPos == playerToken) {
                    score += 2
                } else if (playerInPos != Token.NO_PLAYER) {
                    score -= 2
                }
            } else if (i == 1 || i == 9 || i == 14 || i == 22
                    || i == 7 || i == 11 || i == 12 || i == 16) {
                if (playerInPos == playerToken) {
                    score += 1
                } else if (playerInPos != Token.NO_PLAYER) {
                    score -= 1
                }
            }
        }

        /**
         * Version 0.1
         * Depth: 2, MAX_MOVES: 100 => 53% win vs 6% random win
         * Depth: 3, MAX_MOVES: 100 => 82% win vs 0% random win
         */
        //		score += 100*R1_numPlayerMills + 10*R2_numPlayerTwoPieceConf;
        //		score -= 100*R1_numOppMills + 10*R2_numOppTwoPieceConf;
        //		score += 10*R2_numPlayerTwoPieceConf;
        //		score -= 10*R2_numOppTwoPieceConf;

        /**
         * Version 0.2
         * Depth: 2, MAX_MOVES: 100 => 57% win vs 5% random win
         * Depth: 3, MAX_MOVES: 100 => 83% win vs 0% random win
         * Depth: 4, MAX_MOVES: 100 => 91% win vs 0% random win
         */
        var coef: Int
        // number of mills
        if (gamePhase == Game.PLACING_PHASE) {
            coef = 80
        } else if (gamePhase == Game.MOVING_PHASE) {
            coef = 120
        } else {
            coef = 180
        }
        score += coef * R1_numPlayerMills
        score -= coef * R1_numOppMills

        // number of pieces
        if (gamePhase == Game.PLACING_PHASE) {
            coef = 10
        } else if (gamePhase == Game.MOVING_PHASE) {
            coef = 8
        } else {
            coef = 6
        }
        score += coef * gameBoard.getNumberOfPiecesOfPlayer(playerToken)
        score -= coef * gameBoard.getNumberOfPiecesOfPlayer(opponentPlayer)

        // number of 2 pieces and 1 free spot configuration
        if (gamePhase == Game.PLACING_PHASE) {
            coef = 12
        } else {
            coef = 10
        }
        score += coef * R2_numPlayerTwoPieceConf
        score -= coef * R2_numOppTwoPieceConf

        if (gamePhase == Game.PLACING_PHASE) {
            coef = 10
        } else {
            coef = 25
        }
        return score
    }

    @Throws(GameException::class)
    private fun checkMove(gameBoard: Board, player: Token, moves: MutableList<Move>, move: Move) {
        var madeMill = false
        for (i in 0 until Board.NUM_MILL_COMBINATIONS) { //check if piece made a mill
            var playerPieces = 0
            var selectedPiece = false
            val row = gameBoard.getMillCombination(i)

            for (j in 0 until Board.NUM_POSITIONS_IN_EACH_MILL) {

                if (row[j].playerOccupyingIt == player) {
                    playerPieces++
                }
                if (row[j].positionIndex == move.destIndex) {
                    selectedPiece = true
                }
            }

            if (playerPieces == 3 && selectedPiece) { // made a mill - select piece to remove
                madeMill = true

                for (l in 0 until Board.NUM_POSITIONS_OF_BOARD) {
                    val pos = gameBoard.getPosition(l)

                    if (pos.playerOccupyingIt != player && pos.playerOccupyingIt != Token.NO_PLAYER) {
                        move.removePieceOnIndex = l

                        // add a move for each piece that can be removed, this way it will check what's the best one to remove
                        moves.add(move)
                        movesThatRemove = movesThatRemove + 1 // TODO TESTING
                    }
                }
            }
            selectedPiece = false
        }

        if (!madeMill) { // don't add repeated moves
            moves.add(move)
        } else {
            madeMill = false
        }
    }

    @Throws(GameException::class)
    private fun generateMoves(gameBoard: Board, player: Token, gamePhase: Int): List<Move> {
        val moves = ArrayList<Move>()
        var position: Position
        var adjacentPos: Position

        try {
            if (gamePhase == Game.PLACING_PHASE) {
                for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) { // Search for empty cells and add to the List
                    val move = Move(-7, -1, -1, Move.PLACING)

                    position = gameBoard.getPosition(i)
                    if (!(position).isOccupied) {
                        position.setAsOccupied(player)
                        move.destIndex = i
                        checkMove(gameBoard, player, moves, move)
                        position.setAsUnoccupied()
                    }
                }
            } else if (gamePhase == Game.MOVING_PHASE) {
                for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
                    position = gameBoard.getPosition(i)

                    if ((position).playerOccupyingIt == player) { // for each piece of the player
                        val adjacent = position.adjacentPositionsIndexes

                        for (j in adjacent.indices) { // check valid moves to adjacent positions
                            val move = Move(i, -1, -1, Move.MOVING)
                            adjacentPos = gameBoard.getPosition(adjacent[j])

                            if (!adjacentPos.isOccupied) {
                                adjacentPos.setAsOccupied(player)
                                move.destIndex = adjacent[j]
                                position.setAsUnoccupied()
                                checkMove(gameBoard, player, moves, move)
                                position.setAsOccupied(player)
                                adjacentPos.setAsUnoccupied()
                            }
                        }
                    }
                }
            } else if (gamePhase == Game.FLYING_PHASE) {
                val freeSpaces = ArrayList<Int>()
                val playerSpaces = ArrayList<Int>()

                for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
                    position = gameBoard.getPosition(i)
                    if ((position).playerOccupyingIt == player) {
                        playerSpaces.add(i)
                    } else if (!position.isOccupied) {
                        freeSpaces.add(i)
                    }
                }

                // for every piece the player has on the board
                for (n in playerSpaces.indices) {
                    val srcPos = gameBoard.getPosition(playerSpaces[n])
                    srcPos.setAsUnoccupied()

                    // each empty space is a valid move
                    for (j in freeSpaces.indices) {
                        val move = Move(srcPos.positionIndex, -1, -1, Move.MOVING)
                        val destPos = gameBoard.getPosition(freeSpaces[j])
                        destPos.setAsOccupied(player)
                        move.destIndex = freeSpaces[j]
                        checkMove(gameBoard, player, moves, move)
                        destPos.setAsUnoccupied()
                    }
                    srcPos.setAsOccupied(player)
                }
            }
        } catch (e: GameException) {
            e.printStackTrace()
            System.exit(-1)
        }

        /**
         * => V.0.2
         */
        // if depth > 3, rate the moves and sort them.
        // When depth is 3 or less, this overhead doesn't compensate the time lost
        if (depth > 3) {
            for (move in moves) {
                var removedPlayer = Token.NO_PLAYER
                position = gameBoard.getPosition(move.destIndex)

                // Try this move for the current player
                position.setAsOccupied(player)

                if (gamePhase == Game.PLACING_PHASE) {
                    gameBoard.incNumPiecesOfPlayer(player)
                } else {
                    gameBoard.getPosition(move.srcIndex).setAsUnoccupied()
                }

                if (move.removePieceOnIndex != -1) { // this move removed a piece from opponent
                    val removed = gameBoard.getPosition(move.removePieceOnIndex)
                    removedPlayer = removed.playerOccupyingIt!!
                    removed.setAsUnoccupied()
                    gameBoard.decNumPiecesOfPlayer(removedPlayer)
                }

                move.score = evaluate(gameBoard, gamePhase)

                // Undo move
                position.setAsUnoccupied()

                if (gamePhase == Game.PLACING_PHASE) {
                    gameBoard.decNumPiecesOfPlayer(player)
                } else {
                    gameBoard.getPosition(move.srcIndex).setAsOccupied(player)
                }

                if (move.removePieceOnIndex != -1) {
                    gameBoard.getPosition(move.removePieceOnIndex).setAsOccupied(removedPlayer)
                    gameBoard.incNumPiecesOfPlayer(removedPlayer)
                }
            }

            if (player == playerToken) {
                Collections.sort(moves, HeuristicComparatorMax())
            } else {
                Collections.sort(moves, HeuristicComparatorMin())
            }
        }

        /**
         * V.0.2 <=
         */
        numberOfMoves = numberOfMoves + moves.size
        return moves
    }

    fun getGamePhase(gameBoard: Board, player: Token): Int {
        var gamePhase = Game.PLACING_PHASE
        try {
            if (gameBoard.numTotalPiecesPlaced == Game.NUM_PIECES_PER_PLAYER * 2) {
                gamePhase = Game.MOVING_PHASE
                if (gameBoard.getNumberOfPiecesOfPlayer(player) <= 3) {
                    gamePhase = Game.FLYING_PHASE
                }
            }
        } catch (e: GameException) {
            e.printStackTrace()
            System.exit(-1)
        }

        return gamePhase
    }

    private fun checkGameOver(gameBoard: Board): Int {
        if (gameBoard.numTotalPiecesPlaced == Game.NUM_PIECES_PER_PLAYER * 2) {
            try {
                return if (gameBoard.getNumberOfPiecesOfPlayer(playerToken) <= Game.MIN_NUM_PIECES) {
                    -maxScore
                } else if (gameBoard.getNumberOfPiecesOfPlayer(opponentPlayer) <= Game.MIN_NUM_PIECES) {
                    maxScore
                } else {
                    0
                }
            } catch (e: GameException) {
                e.printStackTrace()

            }

        }
        return 0
    }

    private inner class HeuristicComparatorMax : Comparator<Move> {

        override fun compare(t: Move, t1: Move): Int {
            return t1.score - t.score
        }
    }

    private inner class HeuristicComparatorMin : Comparator<Move> {

        override fun compare(t: Move, t1: Move): Int {
            return t.score - t1.score
        }
    }

    companion object {
        internal val maxScore = 1000000
    }
}
