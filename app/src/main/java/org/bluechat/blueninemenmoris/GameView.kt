package org.bluechat.blueninemenmoris

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View

import org.bluechat.blueninemenmoris.model.Actor
import org.bluechat.blueninemenmoris.model.Board
import org.bluechat.blueninemenmoris.model.GameException
import org.bluechat.blueninemenmoris.model.HumanPlayer
import org.bluechat.blueninemenmoris.model.LocalGame
import org.bluechat.blueninemenmoris.model.Player
import org.bluechat.blueninemenmoris.model.Token

class GameView @Throws(GameException::class)
constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var finalPaint: Paint? = null
    private var blurPaint: Paint? = null  //painting
    var viewHeight: Int = 0
        private set
    var viewWidth: Int = 0
        private set
    private var wt: Int = 0
    private var ht: Int = 0
    private var squareSpace: Int = 0
    var squareStartY: Int = 0
        private set
    var squareStartX: Int = 0
        private set //for board drawing
    var startPieceX: Int = 0
        private set
    var startPieceY1: Int = 0
        private set
    var startPieceY2: Int = 0
        private set
    var p1rx: Int = 0
        private set // for x of removed piece
    private var removedSpace: Int = 0 //for spacing the pieces

    var game: LocalGame? = null   //game
        set(value) {
            field = value
            this.board = field!!.gameBoard
        }

    var board: Board? = null     //gameBoard
    private var graphic: BitmapDrawable? = null
    private val bitmap: Bitmap
    private val bitmap2: Bitmap   //stone bitmaps

    private var h: Handler? = null
    private val r = Runnable { invalidate() }

    val p2rx: Int
        get() = viewWidth - p1rx

    init {

        game = LocalGame()
        val p1 = HumanPlayer("sam", Token.PLAYER_1, 9)
        val p2 = HumanPlayer("aac", Token.PLAYER_2, 9)
        game!!.setPlayers(p1, p2)
        board = game!!.gameBoard

        graphic = context.resources.getDrawable(R.mipmap.stone_b) as BitmapDrawable
        bitmap = graphic!!.bitmap
        graphic = context.resources.getDrawable(R.mipmap.stone_w) as BitmapDrawable
        bitmap2 = graphic!!.bitmap

        init()
    }

    fun init() {

        wt = bitmap.width / 2
        ht = bitmap.height / 2
        h = Handler()

        finalPaint = Paint()
        finalPaint!!.isAntiAlias = true
        finalPaint!!.strokeWidth = 5f
        finalPaint!!.style = Paint.Style.FILL

        blurPaint = Paint()
        blurPaint!!.isAntiAlias = true
        blurPaint!!.alpha = 140
        blurPaint!!.style = Paint.Style.STROKE
        blurPaint!!.color = Color.parseColor("#2196fe")
        blurPaint!!.strokeWidth = 6f

    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)

        viewWidth = xNew
        viewHeight = yNew

        Log.d("gameview", "$viewHeight $viewWidth")

        squareStartY = (viewHeight - viewWidth) / 2
        squareStartX = viewWidth / 8
        board!!.setPosXY(squareStartX, squareStartY)


        squareSpace = squareStartY

        startPieceX = viewWidth / 10
        startPieceY1 = squareStartY
        startPieceY2 = viewWidth + startPieceY1
        game!!.player1!!.setActors(startPieceX, startPieceY1)
        game!!.player2!!.setActors(startPieceX, startPieceY2)

        removedSpace = viewWidth / 10
        p1rx = bitmap.width / 2 + 1
        if (squareStartX > 4 * p1rx) {
            p1rx = squareStartX / 2
        }


    }

    fun getP1ry(removedPieceP1: Int): Int {
        return squareSpace + (removedPieceP1 + 1) * removedSpace
    }

    fun getP2ry(removedPieceP2: Int): Int {
        return viewWidth + squareSpace - removedPieceP2 * removedSpace
    }

    override fun onDraw(c: Canvas) {

        c.drawLine(board!!.getX(0).toFloat(), board!!.getY(0).toFloat(), board!!.getX(2).toFloat(), board!!.getY(2).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(23).toFloat(), board!!.getY(23).toFloat(), board!!.getX(2).toFloat(), board!!.getY(2).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(23).toFloat(), board!!.getY(23).toFloat(), board!!.getX(21).toFloat(), board!!.getY(21).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(0).toFloat(), board!!.getY(0).toFloat(), board!!.getX(21).toFloat(), board!!.getY(21).toFloat(), blurPaint!!)

        c.drawLine(board!!.getX(5).toFloat(), board!!.getY(5).toFloat(), board!!.getX(20).toFloat(), board!!.getY(20).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(5).toFloat(), board!!.getY(5).toFloat(), board!!.getX(3).toFloat(), board!!.getY(3).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(18).toFloat(), board!!.getY(18).toFloat(), board!!.getX(3).toFloat(), board!!.getY(3).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(18).toFloat(), board!!.getY(18).toFloat(), board!!.getX(20).toFloat(), board!!.getY(20).toFloat(), blurPaint!!)

        c.drawLine(board!!.getX(6).toFloat(), board!!.getY(6).toFloat(), board!!.getX(8).toFloat(), board!!.getY(8).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(6).toFloat(), board!!.getY(6).toFloat(), board!!.getX(15).toFloat(), board!!.getY(15).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(17).toFloat(), board!!.getY(17).toFloat(), board!!.getX(15).toFloat(), board!!.getY(15).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(17).toFloat(), board!!.getY(17).toFloat(), board!!.getX(8).toFloat(), board!!.getY(8).toFloat(), blurPaint!!)

        c.drawLine(board!!.getX(1).toFloat(), board!!.getY(1).toFloat(), board!!.getX(7).toFloat(), board!!.getY(7).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(11).toFloat(), board!!.getY(11).toFloat(), board!!.getX(9).toFloat(), board!!.getY(9).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(16).toFloat(), board!!.getY(16).toFloat(), board!!.getX(22).toFloat(), board!!.getY(22).toFloat(), blurPaint!!)
        c.drawLine(board!!.getX(12).toFloat(), board!!.getY(12).toFloat(), board!!.getX(14).toFloat(), board!!.getY(14).toFloat(), blurPaint!!)


        for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
            c.drawCircle(board!!.getX(i).toFloat(), board!!.getY(i).toFloat(), 10f, finalPaint!!)
        }

        val actors1 = game!!.player1!!.actors
        for (actor in actors1) {
            if (actor != null) {
                c.drawBitmap(bitmap, (actor.posx - wt).toFloat(), (actor.posy - ht).toFloat(), null)
            }
        }

        val actors2 = game!!.player2!!.actors
        for (actor in actors2) {
            if (actor != null) {
                c.drawBitmap(bitmap2, (actor.posx - wt).toFloat(), (actor.posy - ht).toFloat(), null)
            }
        }

        h!!.postDelayed(r, 20)
    }

    fun stopHandler() {
        h!!.removeCallbacksAndMessages(null)
    }
}
