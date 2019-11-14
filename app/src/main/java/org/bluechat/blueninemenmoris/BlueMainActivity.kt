package org.bluechat.blueninemenmoris

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import org.bluechat.blueninemenmoris.Bluetooth.BluetoothChatService
import org.bluechat.blueninemenmoris.Bluetooth.Constants
import org.bluechat.blueninemenmoris.model.Actor
import org.bluechat.blueninemenmoris.model.Board
import org.bluechat.blueninemenmoris.model.Game
import org.bluechat.blueninemenmoris.model.GameException
import org.bluechat.blueninemenmoris.model.HumanPlayer
import org.bluechat.blueninemenmoris.model.LocalGame
import org.bluechat.blueninemenmoris.model.Player
import org.bluechat.blueninemenmoris.model.Token

class BlueMainActivity : AppCompatActivity() {
    private var mOutStringBuffer: StringBuffer? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mChatService: BluetoothChatService? = null

    internal var myTurn = false

    private var game: LocalGame? = null
    private var board: Board? = null
    private var gameView: GameView? = null
    private var currActor: Actor? = null
    private var currentBlueActor: Actor? = null
    private var typeface: Typeface? = null
    private var typeface1: Typeface? = null
    private var p1: Player? = null
    private var p2: Player? = null
    private var myToken = Token.NO_PLAYER

    private var top: TextView? = null
    private var topDesc: TextView? = null
    private var bottom: TextView? = null
    private var bottomDesc: TextView? = null

    internal var scaleX = 1f
    internal var scaleY = 1f

    private var backButtonCount = 0
    private var backButtonPreviousTime: Long = 0
    private var backButtonMessageHasBeenShown = false

    private var numberMoves = 0
    private val draws = 0
    private val p1Wins = 0
    private val p2Wins = 0
    private var removedPieceP1: Int = 0
    private var removedPieceP2: Int = 0

    private var madeAMill = false
    private var offsetX: Int = 0
    private var offsetY: Int = 0

    internal var gameListner: View.OnTouchListener = View.OnTouchListener { v, event ->
        val action = event.action
        if (game!!.currentTurnPlayer!!.playerToken === myToken) {
            if (action == MotionEvent.ACTION_DOWN) {
                val y = event.y.toInt()
                val x = event.x.toInt()

                var min = 10000

                if (madeAMill) {
                    val actorsCurrent = game!!.opponentPlayer!!.actors
                    for (actor in actorsCurrent) {
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
                    val actorsCurrent = game!!.currentTurnPlayer!!.actors
                    for (actor in actorsCurrent) {
                        if (game!!.currentGamePhase == Game.PLACING_PHASE) {
                            if (actor == null) continue
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
                                }
                            }
                        }
                    }
                }
                Log.d("currentMin", " $min")
                if (min > 100) {
                    currActor = null

                } else {
                    sendBlue().execute(Constants.SELECT.toString() + "-" + currActor!!.number + "-0")
                }

            } else if (action == MotionEvent.ACTION_MOVE) {
                val y = event.y.toInt()
                val x = event.x.toInt()

                if (currActor != null) {
                    currActor!!.setPosxy(x - offsetX, y - offsetY)
                    // final long currentTime = System.currentTimeMillis();
                    //                        if((currentTime - previousTime) >= 333) {
                    //                           // new sendBlue().execute(Constants.MOVE + "-" + x + "-" + y + "-");
                    //                            previousTime = currentTime;
                    //                        }
                    // msg = ;
                    // new sendBlue().execute(Constants.MOVE + "-" + x + "-" + y + "-");;
                }

            } else if (action == MotionEvent.ACTION_UP) {

                val y = event.y.toInt()
                val x = event.x.toInt()

                Log.d("action", "up")
                var min = 1000
                var mini = -1
                if (currActor != null) {
                    if (madeAMill) {
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

                    val p = game!!.currentTurnPlayer
                    var boardIndex: Int
                    if (min < 100 && mini != -1) {
                        Log.d("current game phase", "  ->  " + game!!.currentGamePhase)
                        boardIndex = mini
                        if (game!!.currentGamePhase == Game.PLACING_PHASE) {
                            try {
                                if (madeAMill) {
                                    val opponentPlayer = if (p!!.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                                    if (game!!.removePiece(boardIndex, opponentPlayer)) {
                                        println("removed piece at $boardIndex")
                                        if (opponentPlayer === Token.PLAYER_1) {
                                            ++removedPieceP1
                                            currActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))
                                        } else {
                                            ++removedPieceP2
                                            currActor!!.setPosxy(gameView!!.p2rx, gameView!!.getP2ry(removedPieceP2))
                                        }

                                        sendBlue().execute(Constants.REMOVE.toString() + "-" + currActor!!.number + "-" + boardIndex + "-")

                                        currActor!!.isRemoved = true
                                        madeAMill = false
                                        game!!.updateCurrentTurnPlayer()

                                    } else {
                                        println("You can't remove a piece from there. Try again")
                                    }
                                } else {
                                    if (game!!.placePieceOfPlayer(boardIndex, p!!.playerToken)) {
                                        Log.d("selected", "pos $mini")

                                        p.raiseNumPiecesOnBoard()

                                        sendBlue().execute(Constants.PLACE.toString() + "-" + currActor!!.number + "-" + boardIndex + "-")

                                        currActor!!.setPosxy(board!!.getX(mini), board!!.getY(mini))
                                        currActor!!.placedIndex = mini

                                        if (game!!.madeAMill(boardIndex, p.playerToken)) {
                                            madeAMill = true
                                            println("You made a mill. You can remove a piece of your oponent: ")
                                        } else {
                                            println("changed current Player")
                                            game!!.updateCurrentTurnPlayer()
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
                                if (!game!!.isTheGameOver && numberMoves < MAX_MOVES) {
                                    if (madeAMill) {
                                        boardIndex = currActor!!.placedIndex
                                        val opponentPlayer = if (p!!.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                                        if (game!!.removePiece(boardIndex, opponentPlayer)) {
                                            println("removed piece at $boardIndex")
                                            if (opponentPlayer === Token.PLAYER_1) {
                                                ++removedPieceP1
                                                currActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))

                                            } else {
                                                ++removedPieceP2
                                                currActor!!.setPosxy(gameView!!.p2rx, gameView!!.getP2ry(removedPieceP2))
                                            }

                                            sendBlue().execute(Constants.REMOVE.toString() + "-" + currActor!!.number + "-" + boardIndex + "-")

                                            currActor!!.isRemoved = true
                                            madeAMill = false

                                            game!!.updateCurrentTurnPlayer()

                                            //TODO : send and update to bluetooth device

                                        } else {
                                            println("You can't remove a piece from there. Try again")
                                        }
                                    } else {
                                        val srcIndex: Int
                                        val destIndex: Int
                                        srcIndex = currActor!!.placedIndex
                                        destIndex = mini
                                        println("Move piece from $srcIndex to $destIndex")

                                        val result: Int = game!!.movePieceFromTo(srcIndex, destIndex, p!!.playerToken)
                                        if ((result) == Game.VALID_MOVE) {

                                            currActor!!.setPosxy(board!!.getX(mini), board!!.getY(mini))
                                            currActor!!.placedIndex = mini

                                            sendBlue().execute(Constants.PLACE.toString() + "-" + currActor!!.number + "-" + destIndex + "-")
                                            if (game!!.madeAMill(destIndex, p!!.playerToken)) {
                                                madeAMill = true
                                            } else {
                                                game!!.updateCurrentTurnPlayer()
                                                println("changed current Player")

                                            }
                                        } else {
                                            currActor!!.setPosxy(board!!.getX(srcIndex), board!!.getY(srcIndex))
                                            println("Invalid move. Error code: $result")
                                        }
                                    }
                                }
                                if (game!!.isTheGameOver) {
                                    val finishLine: String
                                    val finishDesc: String
                                    if ((game as LocalGame).opponentPlayer!!.playerToken === myToken) {
                                        Settings.addGame(applicationContext, "win")
                                        finishLine = "Winner!!"
                                        finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game"
                                    } else {
                                        Settings.addGame(applicationContext, "lost")
                                        finishLine = "Busted!!"
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
                    }
                } else {
                    currActor = null
                }
            }
        }
        true
    }
    private var previousInpB = 1324
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val activity = parent
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        Log.d(TAG, "handleMessage:  connected")
                        val message = 3344.toString() + "-" + Settings.pName + "-" + gameView!!.viewWidth + "-" + gameView!!.viewHeight
                        sendMsg(message)
                    }
                    BluetoothChatService.STATE_CONNECTING -> Log.d(TAG, "handleMessage: connecting")
                    BluetoothChatService.STATE_LISTEN -> {
                    }
                    BluetoothChatService.STATE_NONE -> Log.d(TAG, "handleMessage: state none")
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    val writeMessage = String(writeBuf)
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    val readMessage = String(readBuf, 0, msg.arg1)
                    Log.d(TAG, "handleMessage: $readMessage")
                    val m = readMessage.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    if (previousInpB != Constants.UP) {
                        previousInpB = Integer.parseInt(m[0])
                        if (m.size == 3) {
                            Log.d("received :3", Integer.parseInt(m[0]).toString() + " " + Integer.parseInt(m[1]) + " " + Integer.parseInt(m[2]))
                            bluetoothInput(Integer.parseInt(m[0]), Integer.parseInt(m[1]), Integer.parseInt(m[2]))
                        } else if (m.size == 4) {
                            bluetoothInput(Integer.parseInt(m[0]), m[1], Integer.parseInt(m[2]), Integer.parseInt(m[3]))
                            Log.d("received :4", Integer.parseInt(m[0]).toString() + " " + m[1] + " " + Integer.parseInt(m[2]) + " " + Integer.parseInt(m[3]))
                        } else {
                            val inps = m.size / 3
                            for (i in 0 until inps) {
                                bluetoothInput(Integer.parseInt(m[i * 3]), Integer.parseInt(m[i * 3 + 1]), Integer.parseInt(m[i * 3 + 2]))
                                Log.d("received :5", Integer.parseInt(m[i * 3]).toString() + " " + Integer.parseInt(m[i * 3 + 1]) + " " + Integer.parseInt(m[i * 3 + 2]))
                            }
                        }
                    } else {
                        if (Integer.parseInt(m[0]) != Constants.UP) {
                            previousInpB = Integer.parseInt(m[0])
                            if (m.size == 3) {
                                Log.d("received :3", Integer.parseInt(m[0]).toString() + " " + Integer.parseInt(m[1]) + " " + Integer.parseInt(m[2]))
                                bluetoothInput(Integer.parseInt(m[0]), Integer.parseInt(m[1]), Integer.parseInt(m[2]))
                            } else if (m.size == 4) {
                                bluetoothInput(Integer.parseInt(m[0]), m[1], Integer.parseInt(m[2]), Integer.parseInt(m[3]))
                                Log.d("received :4", Integer.parseInt(m[0]).toString() + " " + m[1] + " " + Integer.parseInt(m[2]) + " " + Integer.parseInt(m[3]))
                            } else {
                                val inps = m.size / 3
                                for (i in 0 until inps) {
                                    bluetoothInput(Integer.parseInt(m[i * 3]), Integer.parseInt(m[i * 3 + 1]), Integer.parseInt(m[i * 3 + 2]))
                                    Log.d("received :5", Integer.parseInt(m[i * 3]).toString() + " " + Integer.parseInt(m[i * 3 + 1]) + " " + Integer.parseInt(m[i * 3 + 2]))
                                }
                            }
                        }
                    }
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    val mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to " + mConnectedDeviceName!!, Toast.LENGTH_SHORT).show()
                    }
                }
                Constants.MESSAGE_TOAST -> if (null != activity) {
                    Toast.makeText(activity, msg.data.getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun sendMsg(msg: String) {
        sendBlue().execute(msg)
    }

    private fun bluetoothInput(msg: Int, name: String, width: Int, height: Int) {
        scaleX = gameView!!.viewWidth.toFloat() / width.toFloat()
        scaleY = gameView!!.viewHeight.toFloat() / height.toFloat()

        if (msg == 3344) {
            if (myTurn) {
                myToken = p1!!.playerToken
                p1!!.name = Settings.pName
                p2!!.name = name
                top!!.text = p1!!.name
                bottom!!.text = name
            } else {
                myToken = p2!!.playerToken
                p1!!.name = name
                p2!!.name = Settings.pName
                top!!.text = name
                bottom!!.text = p2!!.name
            }
            topDesc!!.text = "Start buddy!!"
        }

    }

    private fun bluetoothInput(msg: Int, x: Int, y: Int) {
        var x = x
        var y = y
        //Log.d(TAG, "bluetoothInput: x = " + x + " y = " + y);
        if (msg == Constants.DOWN || msg == Constants.UP || msg == Constants.MOVE) {
            x = (x * scaleX).toInt()
            y = (y * scaleY).toInt()
        }
        //Log.d(TAG, "scaled to : x = " + x + " y = " + y);
        when (msg) {
            Constants.SELECT -> if (madeAMill) {
                currentBlueActor = game!!.opponentPlayer!!.getActorByNumber(x)
            } else {
                currentBlueActor = game!!.currentTurnPlayer!!.getActorByNumber(x)
            }

            Constants.PLACE -> if (currentBlueActor != null) {
                if (game!!.currentGamePhase == Game.PLACING_PHASE) {

                    try {
                        game!!.placePieceOfPlayer(y, game!!.currentTurnPlayer!!.playerToken)
                        numberMoves++ // TODO testing
                        totalMoves++
                        game!!.currentTurnPlayer!!.raiseNumPiecesOnBoard()
                        currentBlueActor!!.setPosxy(board!!.getX(y), board!!.getY(y))
                        currentBlueActor!!.placedIndex = y

                        if (game!!.madeAMill(y, game!!.currentTurnPlayer!!.playerToken)) {
                            madeAMill = true
                            currentBlueActor = null
                            println("You made a mill. You can remove a piece of your opponent: ")
                        } else {
                            println("changed current Player")
                            game!!.updateCurrentTurnPlayer()

                            //TODO : send and update to bluetooth device
                        }
                    } catch (e: GameException) {
                        e.printStackTrace()
                    }

                } else {
                    val srcIndex: Int
                    val destIndex: Int
                    srcIndex = currentBlueActor!!.placedIndex
                    destIndex = y
                    println("Move piece from $srcIndex to $destIndex")

                    val result: Int = game!!.movePieceFromTo(srcIndex, destIndex, game!!.currentTurnPlayer!!.playerToken)
                    try {
                        if ((result) == Game.VALID_MOVE) {
                            numberMoves++ // TODO testing
                            totalMoves++
                            currentBlueActor!!.setPosxy(board!!.getX(destIndex), board!!.getY(destIndex))
                            currentBlueActor!!.placedIndex = destIndex
                            if (game!!.madeAMill(destIndex, game!!.currentTurnPlayer!!.playerToken)) {
                                madeAMill = true
                                currentBlueActor = null
                            } else {
                                game!!.updateCurrentTurnPlayer()
                                println("changed current Player")

                                //TODO : send and update to bluetooth device

                            }
                        } else {
                            currentBlueActor!!.setPosxy(board!!.getX(srcIndex), board!!.getY(srcIndex))
                            println("Invalid move. Error code: $result")

                        }
                    } catch (e: Exception) {

                    }

                    if (game!!.isTheGameOver) {
                        val finishLine: String
                        var finishDesc: String
                        println("Game over. Player " + game!!.opponentPlayer!!.playerToken + " Won")
                        if (game!!.opponentPlayer!!.playerToken === myToken) {
                            Settings.addGame(applicationContext, "win")
                            finishLine = "Winner!!"
                            finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game?"
                        } else {
                            Settings.addGame(applicationContext, "lost")
                            finishLine = "Busted!!"
                            finishDesc = "Oops!!\n Game lost.\n\n Would you like to play a new game?"
                        }
                        finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game"
                        showDialog(finishLine, finishDesc)
                        numberMoves = 0
                        totalMoves = 0
                        //reset the game
                    }
                }
            }
            Constants.REMOVE -> if (currentBlueActor != null) {
                try {
                    if (madeAMill) {
                        // Log.d("removing at pos", ""+ mini);
                        val opponentPlayer = if (game!!.currentTurnPlayer!!.playerToken === Token.PLAYER_1) Token.PLAYER_2 else Token.PLAYER_1
                        if (game!!.removePiece(y, opponentPlayer)) {
                            println("removed piece at $y")
                            if (opponentPlayer === Token.PLAYER_1) {
                                ++removedPieceP1
                                currentBlueActor!!.setPosxy(gameView!!.p1rx, gameView!!.getP1ry(removedPieceP1))
                                //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                            } else {
                                ++removedPieceP2
                                currentBlueActor!!.setPosxy(gameView!!.p2rx, gameView!!.getP2ry(removedPieceP2))
                                //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                            }

                            currentBlueActor!!.isRemoved = true
                            madeAMill = false
                            game!!.updateCurrentTurnPlayer()

                            //TODO : send and update to bluetooth device

                        } else {
                            println("You can't remove a piece from there. Try again")
                        }
                    }
                } catch (e: GameException) {
                    e.printStackTrace()
                }

                if (game!!.isTheGameOver) {
                    val finishLine: String
                    val finishDesc: String
                    println("Game over. Player " + game!!.opponentPlayer!!.playerToken + " Won")
                    if ((game as LocalGame).opponentPlayer!!.playerToken === myToken) {
                        Settings.addGame(applicationContext, "win")
                        finishLine = "Winner!!"
                        finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game?"
                    } else {
                        Settings.addGame(applicationContext, "lost")
                        finishLine = "Busted!!"
                        finishDesc = "Oops!!\n Game lost.\n\n Would you like to play a new game?"
                    }
                    showDialog(finishLine, finishDesc)
                }
            }
            else -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            this.finish()
        }

        typeface = Typeface.createFromAsset(assets,
                "CarterOne.ttf")
        typeface1 = Typeface.createFromAsset(assets,
                "future.otf")

        gameView = findViewById<View>(R.id.gameView) as GameView

        top = findViewById<View>(R.id.top) as TextView
        bottom = findViewById<View>(R.id.bottom) as TextView
        topDesc = findViewById<View>(R.id.topdesc) as TextView
        bottomDesc = findViewById<View>(R.id.bottomdesc) as TextView
        top!!.typeface = typeface
        topDesc!!.typeface = typeface
        bottom!!.typeface = typeface
        bottomDesc!!.typeface = typeface


        Settings.load(applicationContext)
        try {
            game = LocalGame()
            p1 = HumanPlayer("sam", Token.PLAYER_1, 9)
            p2 = HumanPlayer("Heyy!!", Token.PLAYER_2, 9)

            game!!.setPlayers(p1!!, p2!!)
            board = game!!.gameBoard
            gameView!!.game = game!!
        } catch (e: GameException) {
            e.printStackTrace()
        }

        refresh()
    }

    fun refresh() {
        numberMoves = 0
        p1!!.name = Settings.pName
        top!!.text = p1!!.name
        bottom!!.text = "How you doin?"
        topDesc!!.text = "Welcome to Blue Men's Morris"
        bottomDesc!!.text = "Click on right to connect"
        gameView!!.setOnTouchListener(gameListner)
        gameView!!.invalidate()
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
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        val finishnewgame = view.findViewById<View>(R.id.finishnewgame) as Button
        finishnewgame.typeface = typeface

        finishnewgame.setOnClickListener {
            //save the achiewments

            alertDialog.cancel()
        }
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show()
    }

    private fun setupChat() {
        Log.d(TAG, "setupChat()")
        mChatService = BluetoothChatService(this, mHandler)
        mOutStringBuffer = StringBuffer("")
    }

    private fun ensureDiscoverable() {
        if (mBluetoothAdapter!!.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)
        }
    }

    //* Sends a message.(String message)
    @Synchronized
    private fun sendMessage(message: String) {
        if (mChatService!!.state != BluetoothChatService.STATE_CONNECTED) {

            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }

        if (message.length > 0) {
            val send = message.toByteArray()
            mChatService!!.write(send)
            mOutStringBuffer!!.setLength(0)

        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CONNECT_DEVICE_SECURE ->
                // When DeviceListActivity returns with a device to connect
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    connectDevice(data!!, true)

                }
            REQUEST_CONNECT_DEVICE_INSECURE -> if (resultCode == AppCompatActivity.RESULT_OK) {
                connectDevice(data!!, false)
            }
            REQUEST_ENABLE_BT -> if (resultCode == AppCompatActivity.RESULT_OK) {
                setupChat()
            } else {
                Log.d(TAG, "BT not enabled")
                Toast.makeText(this, R.string.bt_not_enabled_leaving,
                        Toast.LENGTH_SHORT).show()
                this.finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //* Establish connection with other divice
    private fun connectDevice(data: Intent, secure: Boolean) {
        val address = data.extras!!
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        mChatService!!.connect(device, secure)
        myTurn = true
    }

    public override fun onStart() {
        super.onStart()
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat()
        }
    }

    fun bluetoothSetup() {
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mChatService != null) {
            mChatService!!.stop()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService!!.state == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService!!.start()
            }

        }
    }

    fun startblue(view: View) {
        val serverIntent = Intent(this, DeviceListActivity::class.java)
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE)
        ensureDiscoverable()
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
            gameView!!.stopHandler()
            //  init();
            alertDialog.cancel()
        }


        settings.setOnClickListener {
            startActivity(Intent(this@BlueMainActivity, Settings::class.java))
            alertDialog.cancel()
        }
        val helps = view.findViewById<View>(R.id.helps) as Button
        helps.typeface = typeface

        helps.setOnClickListener {
            startActivity(Intent(this@BlueMainActivity, AboutActivity::class.java))
            alertDialog.cancel()
        }
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show()
    }

    private inner class sendBlue : AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): Void? {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            sendMessage(params[0])

            return null
        }

        override fun onPostExecute(result: Void) {
            super.onPostExecute(result)
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - backButtonPreviousTime

        backButtonPreviousTime = currentTime

        if (timeDiff < Constants.BACK_PRESS_DELAY || backButtonCount == 0) {
            backButtonCount++
        } else {
            backButtonCount = 1
        }

        if (backButtonCount >= Constants.BACK_PRESS_COUNT) {
            finish()
        }

        if (!backButtonMessageHasBeenShown) {
            val msg = "Press back " + Constants.BACK_PRESS_COUNT + " times to exit"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            backButtonMessageHasBeenShown = true
        }
    }

    companion object {
        private val TAG = "MainActivity"
        // Intent request codes
        private val REQUEST_CONNECT_DEVICE_SECURE = 1
        private val REQUEST_CONNECT_DEVICE_INSECURE = 2
        private val REQUEST_ENABLE_BT = 3
        val MAX_MOVES = 150
        var totalMoves = 0
    }
}