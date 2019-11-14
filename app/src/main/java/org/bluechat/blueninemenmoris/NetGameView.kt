//package org.bluechat.blueninemenmoris
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.RadialGradient
//import android.graphics.Shader
//import android.graphics.Typeface
//import android.graphics.drawable.BitmapDrawable
//import android.os.Handler
//import android.util.AttributeSet
//import android.util.Log
//import android.view.View
//
//import org.bluechat.blueninemenmoris.model.Actor
//import org.bluechat.blueninemenmoris.model.Board
//import org.bluechat.blueninemenmoris.model.GameException
//import org.bluechat.blueninemenmoris.model.HumanPlayer
//import org.bluechat.blueninemenmoris.model.LocalGame
//import org.bluechat.blueninemenmoris.model.NetworkGame
//import org.bluechat.blueninemenmoris.model.Player
//import org.bluechat.blueninemenmoris.model.Token
//
////import org.bluechat.blueninemenmoris.net.Network;
//
//class NetGameView @Throws(GameException::class)
//constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {
//
//    var time = 0
//    private var h: Handler? = null
//
//
//    private var testPaint: Paint? = null
//    private var textPaint: Paint? = null                // for painting the text
//    private var finalPaint: Paint? = null
//    private var blurPaint: Paint? = null
//
//    private var viewHeight: Int = 0
//    private var viewWidth: Int = 0
//
//    private var squareSpace: Int = 0
//    var p1rx: Int = 0
//        private set // for x of removed piece
//    private var removedSpace: Int = 0 //for spacing the pieces
//    var board: Board? = null     //gameboard
//    private var game: NetworkGame? = null   //localgame
//    private var gamesStart: Long = 0
//
//    private var graphic: BitmapDrawable? = null
//    internal var bitmap: Bitmap
//    internal var bitmap2: Bitmap
//    internal var wt: Int = 0
//    internal var ht: Int = 0
//
//    private val FRAME_RATE = 30
//    private var starttimeinsec = 0
//    val p2rx: Int
//        get() = viewWidth - p1rx
//
//    private val r = Runnable { invalidate() }
//
//    init {
//
//        game = NetworkGame()
//        val p1 = HumanPlayer("sam", Token.PLAYER_1, 9)
//        val p2 = HumanPlayer("sam", Token.PLAYER_1, 9)
//
//        game!!.setPlayer(p1)
//        game!!.opponent = p2
//        board = game!!.gameBoard
//
//
//        graphic = context.resources.getDrawable(R.mipmap.stone_b) as BitmapDrawable
//        bitmap = graphic!!.bitmap
//        graphic = context.resources.getDrawable(R.mipmap.stone_w) as BitmapDrawable
//        bitmap2 = graphic!!.bitmap
//
//        init()
//    }
//
//    fun init() {
//
//        wt = bitmap.width / 2
//        ht = bitmap.height / 2
//
//        gamesStart = System.nanoTime()
//        h = Handler()
//        starttimeinsec = System.currentTimeMillis().toInt() / 1000
//
//        // ColorFilter colorFilter = new LightingColorFilter(Color.WHITE, Color.parseColor("#1e90ff"));
//        //EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(new float[]{0,0,0},0.5f,10, 3f);
//
//        val shader = RadialGradient(viewWidth.toFloat(), viewHeight.toFloat(), 350f, Color.BLUE, Color.parseColor("#2979ff"), Shader.TileMode.CLAMP)
//
//        testPaint = Paint()
//        testPaint!!.color = Color.MAGENTA
//        testPaint!!.isAntiAlias = true
//        testPaint!!.strokeWidth = 5f
//        testPaint!!.strokeJoin = Paint.Join.ROUND
//        testPaint!!.style = Paint.Style.STROKE
//
//        finalPaint = Paint()
//        finalPaint!!.isAntiAlias = true
//        finalPaint!!.strokeWidth = 5f
//        finalPaint!!.style = Paint.Style.FILL
//        // finalPaint.setShader(shader);
//
//        blurPaint = Paint()
//        blurPaint!!.isAntiAlias = true
//        blurPaint!!.alpha = 140
//        blurPaint!!.style = Paint.Style.STROKE
//        blurPaint!!.color = Color.parseColor("#2196fe")
//        blurPaint!!.strokeWidth = 6f
//
//
//        textPaint = Paint()
//        textPaint!!.color = Color.parseColor("#76ff03")
//        textPaint!!.textSize = 32f
//        textPaint!!.typeface = Typeface.SERIF
//        textPaint!!.isAntiAlias = true
//        textPaint!!.setShadowLayer(9f, 3f, 3f, Color.parseColor("#2090ff"))
//    }
//
//    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
//        super.onSizeChanged(xNew, yNew, xOld, yOld)
//
//        viewWidth = xNew
//        viewHeight = yNew
//        Log.d("gameview", "$viewHeight $viewWidth")
//
//        val squareStartY = (viewHeight - viewWidth) / 2
//        val squareStartX = viewWidth / 8
//
//        board!!.setPosXY(squareStartX, squareStartY)
//
//
//        squareSpace = squareStartY
//
//        val startPieceX = viewWidth / 10
//        val startPieceY2 = viewWidth + squareStartY
//        game!!.player!!.setActors(startPieceX, squareStartY)
//        game!!.opponent!!.setActors(startPieceX, startPieceY2)
//
//        removedSpace = viewWidth / 10
//        p1rx = bitmap.width / 2 + 1
//        if (squareStartX > 4 * p1rx) {
//            p1rx = squareStartX / 2
//        }
//
//
//    }
//
//    fun getP1ry(removedPieceP1: Int): Int {
//        return squareSpace + (removedPieceP1 + 1) * removedSpace
//    }
//
//    fun getP2ry(removedPieceP2: Int): Int {
//        return viewWidth + squareSpace - removedPieceP2 * removedSpace
//    }
//
//    override fun onDraw(c: Canvas) {
//
//        val ctime = System.currentTimeMillis().toInt() / 1000
//        val diff = ctime - starttimeinsec
//        starttimeinsec = ctime
//        // if(focus){
//        time += diff
//        // }
//        val min = time / 60
//        val sec = time % 60
//
//
//        c.drawLine(board!!.getX(0).toFloat(), board!!.getY(0).toFloat(), board!!.getX(2).toFloat(), board!!.getY(2).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(23).toFloat(), board!!.getY(23).toFloat(), board!!.getX(2).toFloat(), board!!.getY(2).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(23).toFloat(), board!!.getY(23).toFloat(), board!!.getX(21).toFloat(), board!!.getY(21).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(0).toFloat(), board!!.getY(0).toFloat(), board!!.getX(21).toFloat(), board!!.getY(21).toFloat(), blurPaint!!)
//
//        c.drawLine(board!!.getX(5).toFloat(), board!!.getY(5).toFloat(), board!!.getX(20).toFloat(), board!!.getY(20).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(5).toFloat(), board!!.getY(5).toFloat(), board!!.getX(3).toFloat(), board!!.getY(3).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(18).toFloat(), board!!.getY(18).toFloat(), board!!.getX(3).toFloat(), board!!.getY(3).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(18).toFloat(), board!!.getY(18).toFloat(), board!!.getX(20).toFloat(), board!!.getY(20).toFloat(), blurPaint!!)
//
//        c.drawLine(board!!.getX(6).toFloat(), board!!.getY(6).toFloat(), board!!.getX(8).toFloat(), board!!.getY(8).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(6).toFloat(), board!!.getY(6).toFloat(), board!!.getX(15).toFloat(), board!!.getY(15).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(17).toFloat(), board!!.getY(17).toFloat(), board!!.getX(15).toFloat(), board!!.getY(15).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(17).toFloat(), board!!.getY(17).toFloat(), board!!.getX(8).toFloat(), board!!.getY(8).toFloat(), blurPaint!!)
//
//        c.drawLine(board!!.getX(1).toFloat(), board!!.getY(1).toFloat(), board!!.getX(7).toFloat(), board!!.getY(7).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(11).toFloat(), board!!.getY(11).toFloat(), board!!.getX(9).toFloat(), board!!.getY(9).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(16).toFloat(), board!!.getY(16).toFloat(), board!!.getX(22).toFloat(), board!!.getY(22).toFloat(), blurPaint!!)
//        c.drawLine(board!!.getX(12).toFloat(), board!!.getY(12).toFloat(), board!!.getX(14).toFloat(), board!!.getY(14).toFloat(), blurPaint!!)
//
//
//        for (i in 0 until Board.NUM_POSITIONS_OF_BOARD) {
//            c.drawCircle(board!!.getX(i).toFloat(), board!!.getY(i).toFloat(), 10f, finalPaint!!)
//            // c.drawBitmap(bitmap, board.getX(i) - wt, board.getY(i) - ht , null);
//        }
//
//        val actors1 = game!!.player!!.actors
//        for (actor in actors1) {
//            c.drawBitmap(bitmap, (actor.posx - wt).toFloat(), (actor.posy - ht).toFloat(), null)
//        }
//
//        for (i in 0..23) {
//            try {
//                if (board!!.getPosition(i).playerOccupyingIt === game!!.opponent!!.playerToken) {
//                    c.drawBitmap(bitmap2, (board!!.getX(i) - wt).toFloat(), (board!!.getY(i) - ht).toFloat(), null)
//                }
//            } catch (e: GameException) {
//                e.printStackTrace()
//            }
//
//        }
//        // c.drawText("TIME  " + Integer.toString(min / 10) + Integer.toString(min % 10) + ":" + Integer.toString(sec / 10) + Integer.toString(sec % 10), 25, 55, textPaint);
//
//        h!!.postDelayed(r, FRAME_RATE.toLong())
//    }
//
//    fun stopHandler() {
//        h!!.removeCallbacks(r)
//    }
//
//    fun getGame(): NetworkGame? {
//        return game
//    }
//
//    fun setGame(game: NetworkGame) {
//        this.game = game
//        this.board = game.gameBoard
//    }
//
//}
