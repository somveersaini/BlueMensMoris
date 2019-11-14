package org.bluechat.blueninemenmoris.model

import java.util.Random

abstract class AIPlayer @Throws(GameException::class)
constructor(player: Token, numPiecesPerPlayer: Int) : Player(player, numPiecesPerPlayer) {

    protected var rand: Random
    var numberOfMoves = 0 // TODO TESTING
    var movesThatRemove = 0

    init {
        rand = Random()
        setName()
    }

    private fun setName() {
        name = randomNames[rand.nextInt(randomNames.size)]
        println("Name of CPU: $name")
    }

    override val isAI: Boolean
        get() = true

    abstract fun getIndexToPlacePiece(gameBoard: Board): Int

    abstract fun getIndexToRemovePieceOfOpponent(gameBoard: Board): Int

    @Throws(GameException::class)
    abstract fun getPieceMove(gameBoard: Board, gamePhase: Int): Move?

    companion object {
        private val randomNames = arrayOf("Albert Einstein", "Stephen Hawking", "Sheldon Cooper", "Dr.House", "Michael Jackson", "Michael Bay", "Mark Zuckerberg", "Alfred Hitchcock", "Amy Whinehouse", "Angelina Jolie", "Arnold Schwarzenegger", "Barak Obama", "Batman", "David Beckham", "Bruce Willis", "Charlie Chaplin", "Clint Eastwood", "Conan O' Brien", "Condoleezza Rice", "Charles Darwin", "Dexter Morgan", "Frodo", "Sauron", "George W Bush", "Hannibal", "Harrison Ford", "Harry Potter", "John Locke", "Johnny Depp", "John Wayne", "Karl Marx", "Larry King", "Leonardo Dicaprio", "Manny Pacquiao", "Marilyn Manson", "Matt Damon", "Meryl Streep", "Mr Bean", "Paris Hilton", "Prince Charles", "Quentin Tarantino", "Robert Pattinson", "Samuel L. Jackson", "Simon Cowell", "Snoop Lion", "Spielberg", "Steven Seagal", "Terminator", "Tom Cruise", "Will Smith", "Nelson Mandela", "Iron Man", "Hulk", "Thor", "Loki", "Captain America", "Black Widow", "Phil Coulson")
    }
}
