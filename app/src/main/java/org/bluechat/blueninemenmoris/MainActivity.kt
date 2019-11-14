package org.bluechat.blueninemenmoris

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

import org.bluechat.blueninemenmoris.model.Actor
import org.bluechat.blueninemenmoris.model.Board
import org.bluechat.blueninemenmoris.model.Game
import org.bluechat.blueninemenmoris.model.GameException
import org.bluechat.blueninemenmoris.model.HumanPlayer
import org.bluechat.blueninemenmoris.model.LocalGame
import org.bluechat.blueninemenmoris.model.MinimaxAIPlayer
import org.bluechat.blueninemenmoris.model.Move
import org.bluechat.blueninemenmoris.model.Player
import org.bluechat.blueninemenmoris.model.Token

class MainActivity : AppCompatActivity() {
    lateinit var game: LocalGame
     var currActor: Actor? = null
    lateinit var typeface: Typeface
    lateinit var typeface1: Typeface
    lateinit var p1: Player
    lateinit var p2: Player
     var rActor: Actor? = null
    lateinit var actionActor: Actor
    lateinit var top: TextView
    lateinit var topdesc: TextView
    lateinit var bottom: TextView
    lateinit var bottomdesc: TextView
    private var board: Board? = null
    private var gameView: GameView? = null
    private var numberMoves = 0
    private var removedPieceP1: Int = 0
    private var removedPieceP2: Int = 0

    private var madeamill = false
    private var offsetX: Int = 0
    private var offsetY: Int = 0

    internal var gameListner: View.OnTouchListener = View.OnTouchListener { v, event ->
        val action = event.action

        if (action == MotionEvent.ACTION_DOWN) {
            val y = event.y.toInt()
            val x = event.x.toInt()

            var min = 10000

            if (madeamill) {
                val actorscurrent = game.opponentPlayer!!.actors
                for (actor in actorscurrent) {
                    if (actor == null) continue
                    if (!actor.isRemoved) {
                        val t1 = y - actor.posy
                        val t2 = x - actor.posx
                        val temp = Math.sqrt(Math.abs(t1 * t1 + t2 * t2).toDouble()).toInt()
                        if (temp < min) {
                            min = temp
                            offsetY = t1
                            offsetX = t2
                            currActor = actor
                            actor.isAvailableToRemove = true
                        }
                    }
                }
            } else {
                val actorscurrent = game.currentTurnPlayer!!.actors
                for (actor in actorscurrent) {
                    if (actor == null) continue
                    if (game.currentGamePhase == Game.PLACING_PHASE) {
                        if (!actor.isRemoved && !actor.isPlaced) {
                            val t1 = y - actor.posy
                            val t2 = x - actor.posx
                            val temp = Math.sqrt(Math.abs(t1 * t1 + t2 * t2).toDouble()).toInt()
                            if (temp < min) {
                                min = temp
                                offsetY = t1
                                offsetX = t2
                                currActor = actor
                            }
                        }
                    } else {

                        if (!actor.isRemoved) {
                            val t1 = y - actor.posy
                            val t2 = x - actor.posx
                            val temp = Math.sqrt(Math.abs(t1 * t1 + t2 * t2).toDouble()).toInt()
                            if (temp < min) {
                                min = temp
                                offsetY = t1
                                offsetX = t2
                                currActor = actor
                            }
                        }
                    }
                }
            }
            Log.d("currentmin", " $min")
            if (min > 80) {
                currActor = null
            } else {
                Settings.selectSound()
                if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                    topdesc.text = "Playing..."
                    bottomdesc.text = "Waiting"
                } else {
                    bottomdesc.text = "Playing..."
                    topdesc.text = "Waiting"
                }
                //  Log.d("selected opponent piece", " " + mini);
            }

        } else if (action == MotionEvent.ACTION_MOVE) {

            val y = event.y.toInt()
            val x = event.x.toInt()
            // Log.d("moving", x + " " + y);
            if (currActor != null) {
                currActor!!.setPosxy(x - offsetX, y - offsetY)
            }

        } else if (action == MotionEvent.ACTION_UP) {
            Log.d("action", "up")
            var min = 1000
            var mini = -1
            if (currActor != null) {
                if (madeamill) {
                    mini = currActor!!.placedIndex
                    min = 1
                } else {
                    for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
                        try {
                            if (board!!.positionIsAvailable(i)) {
                                val t1 = board!!.getY(i) - currActor!!.posy
                                val t2 = board!!.getX(i) - currActor!!.posx
                                val temp = Math.sqrt(Math.abs(t1 * t1 + t2 * t2).toDouble()).toInt()
                                if (temp < min) {
                                    min = temp
                                    mini = i
                                }
                            }
                        } catch (e: GameException) {
                            e.printStackTrace()
                        }

                    }
                }

                val p = game.currentTurnPlayer
                var boardIndex: Int
                if (min < 80 && mini != -1) {
                    Log.d("current game phase", "  ->  " + game.currentGamePhase)
                    boardIndex = mini
                    if (game.currentGamePhase == Game.PLACING_PHASE) {
                        // Log.d("placing phase", "onTouchEvent: removing");
                        try {
                            if (madeamill) {
                                // Log.d("removing at pos", ""+ mini);
                                val opponentPlayer = if (p!!.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                                if (game.removePiece(boardIndex, opponentPlayer)) {
                                    println("removed piece at $boardIndex")
                                    if (opponentPlayer === Token.PLAYER_1) {
                                        ++removedPieceP1
                                        currActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))
                                        //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                    } else {
                                        ++removedPieceP2
                                        currActor!!.setPosxy(gameView!!.p2rx, gameView!!.getP2ry(removedPieceP2))
                                        //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                    }

                                    Settings.removeSound()

                                    currActor!!.isRemoved = true
                                    madeamill = false
                                    game.updateCurrentTurnPlayer()
                                    if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                                        topdesc.text = "Playing..."
                                        bottomdesc.text = "Waiting"
                                    } else {
                                        bottomdesc.text = "Playing..."
                                        topdesc.text = "Waiting"
                                    }
                                    if (game.currentTurnPlayer!!.isAI) {
                                        Asyncaiplay().execute()
                                    }
                                } else {
                                    println("You can't remove a piece from there. Try again")
                                }
                            } else {
                                if (game.placePieceOfPlayer(boardIndex, p!!.playerToken)) {
                                    Log.d("selected", "pos $mini")
                                    numberMoves++ // TODO testing
                                    totalMoves++
                                    p.raiseNumPiecesOnBoard()
                                    currActor!!.setPosxy(board!!.getX(mini), board!!.getY(mini))
                                    currActor!!.placedIndex = mini



                                    if (game.madeAMill(boardIndex, p.playerToken)) {
                                        madeamill = true
                                        Settings.millSound()
                                        if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                                            topdesc.text = "Mill!! Choose " + p2.name + "'s stone to remove"
                                            bottomdesc.text = "Watching.."
                                        } else {
                                            bottomdesc.text = "Mill!! Choose " + p1.name + "'s stone to remove"
                                            topdesc.text = "Watching.."
                                        }
                                        println("You made a mill. You can remove a piece of your oponent: ")
                                    } else {
                                        println("changed current Player")
                                        game.updateCurrentTurnPlayer()
                                        Settings.placeSound()
                                        if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                                            topdesc.text = "Your turn"
                                            bottomdesc.text = "Waiting.."
                                        } else {
                                            bottomdesc.text = "Your turn"
                                            topdesc.text = "Waiting.."
                                        }

                                        if (game.currentTurnPlayer!!.isAI) {
                                            Asyncaiplay().execute()
                                        }
                                    }
                                } else {
                                    println("You can't place a piece there. Try again")
                                }
                            }
                        } catch (e: GameException) {
                            e.printStackTrace()
                        }

                    } else {
                        // System.out.println("The pieces are all placed. Starting the fun part... ");
                        try {
                            if (!game.isTheGameOver && numberMoves < MAX_MOVES) {
                                if (madeamill) {
                                    boardIndex = currActor!!.placedIndex
                                    // Log.d("removing at pos", ""+ mini);
                                    val opponentPlayer = if (p!!.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                                    if (game.removePiece(boardIndex, opponentPlayer)) {
                                        println("removed piece at $boardIndex")
                                        if (opponentPlayer === Token.PLAYER_1) {
                                            ++removedPieceP1
                                            currActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))
                                            //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                        } else {
                                            ++removedPieceP2
                                            currActor!!.setPosxy(gameView!!.p2rx, gameView!!.getP2ry(removedPieceP2))
                                            //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                        }

                                        currActor!!.isRemoved = true
                                        madeamill = false

                                        game.updateCurrentTurnPlayer()
                                        Settings.removeSound()
                                        if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                                            topdesc.text = "Your turn!!"
                                            bottomdesc.text = "Waiting..."
                                        } else {
                                            bottomdesc.text = "Your turn!!"
                                            topdesc.text = "Waiting..."
                                        }
                                        if (game.currentTurnPlayer!!.isAI && !game.isTheGameOver) {
                                            Asyncaiplay().execute()
                                        }
                                    } else {
                                        println("You can't remove a piece from there. Try again")
                                    }
                                } else {
                                    val srcIndex: Int
                                    val destIndex: Int
                                    srcIndex = currActor!!.placedIndex
                                    destIndex = mini
                                    println("Move piece from $srcIndex to $destIndex")

                                    val result = game.movePieceFromTo(srcIndex, destIndex, p!!.playerToken)
                                    if (result == Game.VALID_MOVE) {
                                        numberMoves++ // TODO testing
                                        totalMoves++
                                        currActor!!.setPosxy(board!!.getX(mini), board!!.getY(mini))
                                        currActor!!.placedIndex = mini
                                        if (game.madeAMill(destIndex, p!!.playerToken)) {
                                            madeamill = true

                                            Settings.millSound()
                                            if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                                                topdesc.text = "Mill!! Choose " + p2.name + "'s stone to remove"
                                                bottomdesc.text = "Watching.."
                                            } else {
                                                bottomdesc.text = "Mill!! Choose " + p1.name + "'s stone to remove"
                                                topdesc.text = "Watching.."
                                            }

                                        } else {
                                            game.updateCurrentTurnPlayer()
                                            println("changed current Player")
                                            Settings.place1Sound()
                                            if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                                                topdesc.text = "Your turn"
                                                bottomdesc.text = "Waiting..."
                                            } else {
                                                bottomdesc.text = "Your turn"
                                                topdesc.text = "Waiting..."
                                            }
                                            if (game.currentTurnPlayer!!.isAI && !game.isTheGameOver) {
                                                Asyncaiplay().execute()
                                            }
                                        }
                                    } else {
                                        currActor!!.setPosxy(board!!.getX(srcIndex), board!!.getY(srcIndex))
                                        println("Invalid move. Error code: $result")
                                    }
                                }
                            }
                            if (game.isTheGameOver) {
                                val finishLine: String
                                val finishDesc: String
                                Settings.winSound()
                                println("Game over. Player " + game.opponentPlayer!!.playerToken + " Won")
                                if (game.opponentPlayer!!.playerToken === Token.PLAYER_1) {
                                    Settings.addGame(applicationContext, "win")
                                    finishLine = game.player1!!.name + " Win!!"
                                    finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game"
                                } else {
                                    Settings.addGame(applicationContext, "gamelost")
                                    finishLine = game.player2!!.name + " Win!!"
                                    finishDesc = "Oops!!\n Game lost.\n\n Would you like to play a new game"
                                }

                                showDialog(finishLine, finishDesc)
                                numberMoves = 0
                            }
                        } catch (e: GameException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    if (currActor!!.placedIndex == -1) {
                        currActor!!.setToPreviousPosition()
                    } else {
                        currActor!!.setPosxy(board!!.getX(currActor!!.placedIndex), board!!.getY(currActor!!.placedIndex))
                    }
                    Settings.moveSound()
                    if (game.currentTurnPlayer!!.playerToken === Token.PLAYER_1) {
                        topdesc.text = "Your Turn.."
                        bottomdesc.text = "Waiting..."
                    } else {
                        bottomdesc.text = "Your turn.."
                        topdesc.text = "Waiting..."
                    }
                }
            } else {
                currActor = null
            }

        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        typeface = Typeface.createFromAsset(assets,
                "CarterOne.ttf")
        typeface1 = Typeface.createFromAsset(assets,
                "future.otf")

        gameView = findViewById<View>(R.id.gameView) as GameView

        top = findViewById<View>(R.id.top) as TextView
        bottom = findViewById<View>(R.id.bottom) as TextView
        topdesc = findViewById<View>(R.id.topdesc) as TextView
        bottomdesc = findViewById<View>(R.id.bottomdesc) as TextView
        top.typeface = typeface
        topdesc.typeface = typeface
        bottom.typeface = typeface
        bottomdesc.typeface = typeface


        Settings.load(applicationContext)

        try {
            game = LocalGame()
            p1 = HumanPlayer(Settings.pName, Token.PLAYER_1, 9)
            if (intent.getBooleanExtra("isAI", false)) {
                val depth = intent.getIntExtra("level", 3)
                p2 = MinimaxAIPlayer(Token.PLAYER_2, 9, depth)
            } else {
                p2 = HumanPlayer("Opponent", Token.PLAYER_2, 9)
            }
            game.setPlayers(p1, p2)
            board = game.gameBoard
            gameView!!.game = game
        } catch (e: GameException) {
            e.printStackTrace()
        }

        refresh()

    }

    fun init() {
        game = LocalGame()

        p1.reset()
        removedPieceP1 = 0
        p1.setActors(gameView!!.startPieceX, gameView!!.startPieceY1)
        p2.reset()
        removedPieceP2 = 0
        p2.setActors(gameView!!.startPieceX, gameView!!.startPieceY2)
        game.setPlayers(p1, p2)
        board = game.gameBoard
        board!!.setPosXY(gameView!!.squareStartX, gameView!!.squareStartY)
        gameView!!.game = game
        gameView!!.init()
        refresh()

    }

    fun refresh() {
        p1.name = Settings.pName
        top.text = p1.name
        bottom.text = p2.name
        topdesc.text = "Your turn"
        bottomdesc.text = "Waiting.."
        gameView!!.setOnTouchListener(gameListner)
        gameView!!.invalidate()
        Settings.phaseSound()
    }

    fun aiPlay() {
        try {
            val p = game.player2 as MinimaxAIPlayer?
            var boardIndex: Int

            if (game.currentGamePhase == Game.PLACING_PHASE) {
                boardIndex = p!!.getIndexToPlacePiece(board!!)

                val actors = p.actors
                for (actor in actors) {
                    if (actor == null) continue
                    if (!actor.isPlaced && !actor.isRemoved) {
                        numberMoves++ // TODO testing
                        totalMoves++
                        p.raiseNumPiecesOnBoard()
                        val x2 = board!!.getX(boardIndex)
                        val x1 = actor.posx

                        val y2 = board!!.getY(boardIndex)
                        val y1 = actor.posy

                        var pS: Int
                        val px = x2 - x1
                        val py = y2 - y1
                        if (Math.abs(px) > Math.abs(py)) {
                            pS = Math.abs(py)
                        } else {
                            pS = Math.abs(px)
                        }
                        if (pS == 0) {
                            pS = 1
                        }
                        val ySlope = (py / pS).toFloat()
                        val xSlope = (px / pS).toFloat()

                        actionActor = actor
                        for (i in 1..pS) {
                            // actor.setPosxy(x1 + i*xSlope, (int) (y1 + i*ySlope));
                            Log.d("cordinates ", "x : " + (x1 + i * xSlope) + "   y: " + (y1 + i * ySlope))
                            actor.setPosxy((x1 + i * xSlope).toInt(), (y1 + i * ySlope).toInt())
                        }

                        actor.setPosxy(board!!.getX(boardIndex), board!!.getY(boardIndex))
                        actor.placedIndex = boardIndex
                        break
                    }
                }

                if (game.placePieceOfPlayer(boardIndex, p.playerToken)) {

                    if (game.madeAMill(boardIndex, p.playerToken)) {
                        val opponentPlayer = if (p.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                        boardIndex = p.getIndexToRemovePieceOfOpponent(board!!)
                        game.removePiece(boardIndex, opponentPlayer)
                        rActor = game.player1!!.getActorAt(boardIndex)
                        ++removedPieceP1
                        rActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))
                        rActor!!.isRemoved = true
                    }
                    game.updateCurrentTurnPlayer()
                } else {
                    println("You can't place a piece there. Try again")
                }

            } else {
                val srcIndex: Int
                val destIndex: Int
                val move = p!!.getPieceMove(board!!, game.currentGamePhase)
                if (move != null) {
                    srcIndex = move.srcIndex
                    destIndex = move.destIndex
                    println("Move piece from $srcIndex to $destIndex")

                    val result = game.movePieceFromTo(srcIndex, destIndex, p.playerToken)
                    if ((result ) == Game.VALID_MOVE) {
                        numberMoves++ // TODO testing
                        totalMoves++
                        rActor = p.getActorAt(srcIndex)
                        rActor!!.setPosxy(board!!.getX(destIndex), board!!.getY(destIndex))
                        rActor!!.placedIndex = destIndex
                        if (game.madeAMill(destIndex, p.playerToken)) {
                            val opponentPlayer = if (p.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                            boardIndex = p.getIndexToRemovePieceOfOpponent(board!!)
                            game.removePiece(boardIndex, opponentPlayer)
                            rActor = game.player1!!.getActorAt(boardIndex)
                            ++removedPieceP1
                            rActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))
                            rActor!!.isRemoved = true
                        }
                        game.updateCurrentTurnPlayer()
                    }
                } else {
                    val finishLine = p1.name + " Win!! "
                    val finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game?"
                    Settings.addGame(applicationContext, "win")
                    runOnUiThread { showDialog(finishLine, finishDesc) }
                    numberMoves = 0
                }
                if (game.isTheGameOver) {
                    println("Game over. Player " + game.opponentPlayer!!.playerToken + " Won")
                    Settings.addGame(applicationContext, "lost")
                    val finishLine = p2.name + " Win!! "
                    val finishDesc = "Oops!!\n Game lost.\n\n Would you like to play a new game?"
                    runOnUiThread { showDialog(finishLine, finishDesc) }
                    numberMoves = 0
                }
            }
        } catch (e: GameException) {
            e.printStackTrace()
        }

    }

    fun showDialog(finishline: String, finishdesc: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.finished, null)
        val tv1 = view.findViewById<View>(R.id.gamename) as TextView
        tv1.typeface = typeface1
        val line = view.findViewById<View>(R.id.finishline) as TextView
        line.typeface = typeface
        line.text = finishline
        val desc = view.findViewById<View>(R.id.finishdescription) as TextView
        desc.typeface = typeface
        desc.text = finishdesc
        alertDialogBuilder.setView(view)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val finishnewgame = view.findViewById<View>(R.id.finishnewgame) as Button
        finishnewgame.typeface = typeface

        alertDialog.setCancelable(false)

        finishnewgame.setOnClickListener {
            //save the achiewments
            gameView!!.stopHandler()
            init()
            alertDialog.cancel()
        }
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show()
    }

    fun options(v: View) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.opt, null)
        val tv1 = view.findViewById<View>(R.id.gamename) as TextView
        tv1.typeface = typeface1

        alertDialogBuilder.setView(view)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        val finishnewgame = view.findViewById<View>(R.id.finishnewgame) as Button
        finishnewgame.typeface = typeface
        val settings = view.findViewById<View>(R.id.set) as Button
        settings.typeface = typeface

        finishnewgame.setOnClickListener {
            //save the achiewments
            running = false
            gameView!!.stopHandler()
            init()
            alertDialog.cancel()
        }


        settings.setOnClickListener {
            startActivity(Intent(this@MainActivity, Settings::class.java))
            alertDialog.cancel()
        }
        val helps = view.findViewById<View>(R.id.helps) as Button
        helps.typeface = typeface

        helps.setOnClickListener {
            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            alertDialog.cancel()
        }
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show()
    }

    public override fun onResume() {
        super.onResume()
        refresh()
    }

    private inner class Asyncaiplay : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            topdesc.text = "Waiting.."
            bottomdesc.text = "Thinking.."
            gameView!!.isEnabled = false
        }

        override fun doInBackground(vararg params: Void): Void? {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            aiPlay()

            return null
        }

        override fun onPostExecute(result: Void) {
            super.onPostExecute(result)
            //this method will be running on UI thread
            topdesc.text = "Your turn"
            bottomdesc.text = "Waiting.."
            gameView?.isEnabled = true
            gameView?.invalidate()
        }
    }

    companion object {
        val MAX_MOVES = 150
        var totalMoves = 0

        var running = false
    }
}
