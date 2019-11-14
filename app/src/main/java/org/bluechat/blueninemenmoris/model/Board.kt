package org.bluechat.blueninemenmoris.model

import android.util.Log

/**
 * Created by Samsaini on 05/25/2016.
 */
class Board {

    private val boardPositions: Array<Position>
    private var millCombinations: Array<Array<Position>>
    private var numOfPiecesP1: Int = 0
    private var numOfPiecesP2: Int = 0
    var numTotalPiecesPlaced: Int = 0
        private set

    init {
        boardPositions = Array(NUM_POSITIONS_OF_BOARD) { Position() }
        millCombinations = Array(NUM_MILL_COMBINATIONS) { Array(NUM_POSITIONS_IN_EACH_MILL) { Position()} }
        numOfPiecesP1 = 0
        numOfPiecesP2 = 0
        numTotalPiecesPlaced = 0
        initBoard()
        initMillCombinations()
    }

    @Throws(GameException::class)
    fun getPosition(posIndex: Int): Position {
        return if (posIndex >= 0 && posIndex < Board.NUM_POSITIONS_OF_BOARD) {
            boardPositions[posIndex]
        } else {
            throw GameException("" + javaClass.name + " - Invalid Board Position Index: " + posIndex)
        }
    }

    fun getX(posIndex: Int): Int {
        return boardPositions[posIndex].posx
    }

    fun getY(posIndex: Int): Int {
        return boardPositions[posIndex].posy
    }

    @Throws(GameException::class)
    fun positionIsAvailable(posIndex: Int): Boolean {
        return if (posIndex >= 0 && posIndex < Board.NUM_POSITIONS_OF_BOARD) {
            !boardPositions[posIndex].isOccupied
        } else {
            throw GameException("" + javaClass.name + " - Invalid Board Position Index: " + posIndex)
        }
    }

    @Throws(GameException::class)
    fun setPositionAsPlayer(posIndex: Int, player: Token) {
        if (posIndex >= 0 && posIndex < Board.NUM_POSITIONS_OF_BOARD) {
            if (player == Token.PLAYER_1 || player == Token.PLAYER_2) {
                boardPositions[posIndex].setAsOccupied(player)
            } else {
                throw GameException("" + javaClass.name + " - Invalid Player Token: " + player)
            }
        } else {
            throw GameException("" + javaClass.name + " - Invalid Board Position Index: " + posIndex)
        }
    }

    fun incNumTotalPiecesPlaced(): Int {
        return ++numTotalPiecesPlaced
    }

    @Throws(GameException::class)
    fun incNumPiecesOfPlayer(player: Token): Int {
        return when (player) {
            Token.PLAYER_1 -> ++numOfPiecesP1
            Token.PLAYER_2 -> ++numOfPiecesP2
            else -> throw GameException("" + javaClass.name + " - Invalid Player Token: " + player)
        }
    }

    @Throws(GameException::class)
    fun decNumPiecesOfPlayer(player: Token): Int {
        return when (player) {
            Token.PLAYER_1 -> --numOfPiecesP1
            Token.PLAYER_2 -> --numOfPiecesP2
            else -> throw GameException("" + javaClass.name + " - Invalid Player Token: " + player)
        }
    }

    @Throws(GameException::class)
    fun getNumberOfPiecesOfPlayer(player: Token): Int {
        return when (player) {
            Token.PLAYER_1 -> numOfPiecesP1
            Token.PLAYER_2 -> numOfPiecesP2
            else -> throw GameException("" + javaClass.name + " - Invalid Player Token: " + player)
        }
    }

    private fun initBoard() {
        for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
            boardPositions[i] = Position(i)
        }
        // outer square
        boardPositions[0].addAdjacentPositionsIndexes(1, 9)
        boardPositions[1].addAdjacentPositionsIndexes(0, 2, 4)
        boardPositions[2].addAdjacentPositionsIndexes(1, 14)
        boardPositions[9].addAdjacentPositionsIndexes(0, 10, 21)
        boardPositions[14].addAdjacentPositionsIndexes(2, 13, 23)
        boardPositions[21].addAdjacentPositionsIndexes(9, 22)
        boardPositions[22].addAdjacentPositionsIndexes(19, 21, 23)
        boardPositions[23].addAdjacentPositionsIndexes(14, 22)
        // middle square
        boardPositions[3].addAdjacentPositionsIndexes(4, 10)
        boardPositions[4].addAdjacentPositionsIndexes(1, 3, 5, 7)
        boardPositions[5].addAdjacentPositionsIndexes(4, 13)
        boardPositions[10].addAdjacentPositionsIndexes(3, 9, 11, 18)
        boardPositions[13].addAdjacentPositionsIndexes(5, 12, 14, 20)
        boardPositions[18].addAdjacentPositionsIndexes(10, 19)
        boardPositions[19].addAdjacentPositionsIndexes(16, 18, 20, 22)
        boardPositions[20].addAdjacentPositionsIndexes(13, 19)
        // inner square
        boardPositions[6].addAdjacentPositionsIndexes(7, 11)
        boardPositions[7].addAdjacentPositionsIndexes(4, 6, 8)
        boardPositions[8].addAdjacentPositionsIndexes(7, 12)
        boardPositions[11].addAdjacentPositionsIndexes(6, 10, 15)
        boardPositions[12].addAdjacentPositionsIndexes(8, 13, 17)
        boardPositions[15].addAdjacentPositionsIndexes(11, 16)
        boardPositions[16].addAdjacentPositionsIndexes(15, 17, 19)
        boardPositions[17].addAdjacentPositionsIndexes(12, 16)
    }

    fun setPosXY(squareStart: Int, squareStartY: Int) {
        // outer square
        val s1 = 2 * squareStart
        val s2 = 3 * squareStart
        val s3 = 4 * squareStart
        val s4 = 5 * squareStart
        val s5 = 6 * squareStart
        val s6 = 7 * squareStart

        Log.d("boardset sx  xy   ", "$squareStart $squareStartY")

        boardPositions[0].setPosxy(squareStart, squareStart + squareStartY)
        boardPositions[1].setPosxy(squareStart, s3 + squareStartY)
        boardPositions[2].setPosxy(squareStart, s6 + squareStartY)
        boardPositions[3].setPosxy(s1, s1 + squareStartY)
        boardPositions[4].setPosxy(s1, s3 + squareStartY)
        boardPositions[5].setPosxy(s1, s5 + squareStartY)
        boardPositions[6].setPosxy(s2, s2 + squareStartY)
        boardPositions[7].setPosxy(s2, s3 + squareStartY)
        boardPositions[8].setPosxy(s2, s4 + squareStartY)

        boardPositions[9].setPosxy(s3, squareStart + squareStartY)
        boardPositions[10].setPosxy(s3, s1 + squareStartY)
        boardPositions[11].setPosxy(s3, s2 + squareStartY)
        boardPositions[12].setPosxy(s3, s4 + squareStartY)
        boardPositions[13].setPosxy(s3, s5 + squareStartY)
        boardPositions[14].setPosxy(s3, s6 + squareStartY)

        boardPositions[15].setPosxy(s4, s2 + squareStartY)
        boardPositions[16].setPosxy(s4, s3 + squareStartY)
        boardPositions[17].setPosxy(s4, s4 + squareStartY)
        boardPositions[18].setPosxy(s5, s1 + squareStartY)
        boardPositions[19].setPosxy(s5, s3 + squareStartY)
        boardPositions[20].setPosxy(s5, s5 + squareStartY)
        boardPositions[21].setPosxy(s6, squareStart + squareStartY)
        boardPositions[22].setPosxy(s6, s3 + squareStartY)
        boardPositions[23].setPosxy(s6, s6 + squareStartY)
    }

    @Throws(GameException::class)
    fun getMillCombination(index: Int): Array<Position> {
        return if (index in 0 until NUM_MILL_COMBINATIONS) {
            millCombinations[index]
        } else {
            throw GameException("" + javaClass.name + " - Invalid Mill Combination Index: " + index)
        }
    }


    private fun initMillCombinations() {

        //outer square
        millCombinations[0][0] = boardPositions[0]
        millCombinations[0][1] = boardPositions[1]
        millCombinations[0][2] = boardPositions[2]
        millCombinations[1][0] = boardPositions[0]
        millCombinations[1][1] = boardPositions[9]
        millCombinations[1][2] = boardPositions[21]
        millCombinations[2][0] = boardPositions[2]
        millCombinations[2][1] = boardPositions[14]
        millCombinations[2][2] = boardPositions[23]
        millCombinations[3][0] = boardPositions[21]
        millCombinations[3][1] = boardPositions[22]
        millCombinations[3][2] = boardPositions[23]
        //middle square
        millCombinations[4][0] = boardPositions[3]
        millCombinations[4][1] = boardPositions[4]
        millCombinations[4][2] = boardPositions[5]
        millCombinations[5][0] = boardPositions[3]
        millCombinations[5][1] = boardPositions[10]
        millCombinations[5][2] = boardPositions[18]
        millCombinations[6][0] = boardPositions[5]
        millCombinations[6][1] = boardPositions[13]
        millCombinations[6][2] = boardPositions[20]
        millCombinations[7][0] = boardPositions[18]
        millCombinations[7][1] = boardPositions[19]
        millCombinations[7][2] = boardPositions[20]
        //inner square
        millCombinations[8][0] = boardPositions[6]
        millCombinations[8][1] = boardPositions[7]
        millCombinations[8][2] = boardPositions[8]
        millCombinations[9][0] = boardPositions[6]
        millCombinations[9][1] = boardPositions[11]
        millCombinations[9][2] = boardPositions[15]
        millCombinations[10][0] = boardPositions[8]
        millCombinations[10][1] = boardPositions[12]
        millCombinations[10][2] = boardPositions[17]
        millCombinations[11][0] = boardPositions[15]
        millCombinations[11][1] = boardPositions[16]
        millCombinations[11][2] = boardPositions[17]
        //others
        millCombinations[12][0] = boardPositions[1]
        millCombinations[12][1] = boardPositions[4]
        millCombinations[12][2] = boardPositions[7]
        millCombinations[13][0] = boardPositions[9]
        millCombinations[13][1] = boardPositions[10]
        millCombinations[13][2] = boardPositions[11]
        millCombinations[14][0] = boardPositions[12]
        millCombinations[14][1] = boardPositions[13]
        millCombinations[14][2] = boardPositions[14]
        millCombinations[15][0] = boardPositions[16]
        millCombinations[15][1] = boardPositions[19]
        millCombinations[15][2] = boardPositions[22]
    }

    fun printBoard() {
        println(showPos(0) + " - - - - - " + showPos(1) + " - - - - - " + showPos(2))
        println("|           |           |")
        println("|     " + showPos(3) + " - - " + showPos(4) + " - - " + showPos(5) + "     |")
        println("|     |     |     |     |")
        println("|     | " + showPos(6) + " - " + showPos(7) + " - " + showPos(8) + " |     |")
        println("|     | |       | |     |")
        println(showPos(9) + " - - " + showPos(10) + "-" + showPos(11) + "       " + showPos(12) + "-" + showPos(13) + " - - " + showPos(14))
        println("|     | |       | |     |")
        println("|     | " + showPos(15) + " - " + showPos(16) + " - " + showPos(17) + " |     |")
        println("|     |     |     |     |")
        println("|     " + showPos(18) + " - - " + showPos(19) + " - - " + showPos(20) + "     |")
        println("|           |           |")
        println(showPos(21) + " - - - - - " + showPos(22) + " - - - - - " + showPos(23))
    }

    private fun showPos(i: Int): String? {
        return when (boardPositions[i].playerOccupyingIt) {
            Token.PLAYER_1 -> "X"
            Token.PLAYER_2 -> "O"
            Token.NO_PLAYER -> "*"
            else -> null
        }
    }

    companion object {
        const val NUM_POSITIONS_OF_BOARD = 24
        const val NUM_MILL_COMBINATIONS = 16
        const val NUM_POSITIONS_IN_EACH_MILL = 3
    }
}
