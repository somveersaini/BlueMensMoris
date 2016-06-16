package org.bluechat.blueninemenmoris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.bluechat.blueninemenmoris.model.Actor;
import org.bluechat.blueninemenmoris.model.Board;
import org.bluechat.blueninemenmoris.model.GameException;
import org.bluechat.blueninemenmoris.model.HumanPlayer;
import org.bluechat.blueninemenmoris.model.LocalGame;
import org.bluechat.blueninemenmoris.model.NetworkGame;
import org.bluechat.blueninemenmoris.model.Player;
import org.bluechat.blueninemenmoris.model.Token;
import org.bluechat.blueninemenmoris.net.Network;

public class NetGameView extends View {

    public int time = 0;
    private Handler h;


    private Paint testPaint;
    private Paint textPaint;                // for painting the text
    private Paint finalPaint;
    private Paint blurPaint;

    private int viewHeight;
    private int viewWidth;

    private int squareSpace;
    private int removedx; // for x of removed piece
    private int removedSpace; //for spacing the pieces
    private Board board;     //gameboard
    private NetworkGame game;   //localgame
    private  long gamesStart;

    private BitmapDrawable graphic;
    Bitmap bitmap, bitmap2;
    int wt, ht;

    private final int FRAME_RATE = 30;
    private int starttimeinsec = 0;

    public NetGameView(Context context, AttributeSet attrs) throws GameException {
        super(context, attrs);

        game = new NetworkGame();
        Player p1 = new HumanPlayer("sam", Token.PLAYER_1,9);
        Player p2 = new HumanPlayer("sam", Token.PLAYER_1,9);

        game.setPlayer(p1);
        game.setOpponent(p2);
        board = game.getGameBoard();


        graphic = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.stone_b);
        bitmap = graphic.getBitmap();
        graphic = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.stone_w);
        bitmap2 = graphic.getBitmap();

        init();
    }

    public void init(){

        wt = bitmap.getWidth()/2;
        ht = bitmap.getHeight()/2;

        gamesStart = System.nanoTime();
        h = new Handler();
        starttimeinsec = (int) System.currentTimeMillis() / 1000;

        // ColorFilter colorFilter = new LightingColorFilter(Color.WHITE, Color.parseColor("#1e90ff"));
        //EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(new float[]{0,0,0},0.5f,10, 3f);

        Shader shader = new RadialGradient(viewWidth, viewHeight, 350, Color.BLUE, Color.parseColor("#2979ff"), Shader.TileMode.CLAMP);

        testPaint = new Paint();
        testPaint.setColor(Color.MAGENTA);
        testPaint.setAntiAlias(true);
        testPaint.setStrokeWidth(5);
        testPaint.setStrokeJoin(Paint.Join.ROUND);
        testPaint.setStyle(Paint.Style.STROKE);

        finalPaint = new Paint();
        finalPaint.setAntiAlias(true);
        finalPaint.setStrokeWidth(5);
        finalPaint.setStyle(Paint.Style.FILL);
        // finalPaint.setShader(shader);

        blurPaint = new Paint();
        blurPaint.setAntiAlias(true);
        blurPaint.setAlpha(140);
        blurPaint.setStyle(Paint.Style.STROKE);
        blurPaint.setColor(Color.parseColor("#2196fe"));
        blurPaint.setStrokeWidth(6);


        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#76ff03"));
        textPaint.setTextSize(32);
        textPaint.setTypeface(Typeface.SERIF);
        textPaint.setAntiAlias(true);
        textPaint.setShadowLayer(9f, 3, 3, Color.parseColor("#2090ff"));
    }
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
        Log.d("gameview", viewHeight +  " " + viewWidth);

        int  squareStartY =   (viewHeight - viewWidth)/2;
        int squareStartX = viewWidth / 8;

        board.setPosXY(squareStartX,squareStartY);


        squareSpace = squareStartY;

        int startPieceX = viewWidth/10;
        int startPieceY1 = squareStartY;
        int startPieceY2 = viewWidth + startPieceY1;
        game.getPlayer().setActors(startPieceX,startPieceY1);
        game.getOpponent().setActors(startPieceX,startPieceY2);

        removedSpace = viewWidth/10;
        removedx  = bitmap.getWidth()/2 + 1;
        if(squareStartX > 4*removedx){
            removedx = squareStartX /2;
        }


    }
    public  int getP1rx(){
        return removedx;
    }
    public  int getP2rx(){
        return viewWidth - removedx;
    }
    public int getP1ry(int removedPieceP1){
        return (squareSpace) + ((removedPieceP1 + 1) * removedSpace);
    }
    public int getP2ry(int removedPieceP2){
        return (viewWidth + squareSpace) - (removedPieceP2 * removedSpace);
    }

    private Runnable r = new Runnable() {

        public void run() {
            invalidate();
        }
    };

    protected void onDraw(Canvas c) {

        int ctime = (int) System.currentTimeMillis() / 1000;
        int diff = ctime - starttimeinsec;
        starttimeinsec = ctime;
       // if(focus){
            time += diff;
       // }
        int min = time / 60;
        int sec = time % 60;


        c.drawLine(board.getX(0), board.getY(0), board.getX(2), board.getY(2), blurPaint);
        c.drawLine(board.getX(23), board.getY(23), board.getX(2), board.getY(2), blurPaint);
        c.drawLine(board.getX(23), board.getY(23), board.getX(21), board.getY(21), blurPaint);
        c.drawLine(board.getX(0), board.getY(0), board.getX(21), board.getY(21), blurPaint);

        c.drawLine(board.getX(5), board.getY(5), board.getX(20), board.getY(20), blurPaint);
        c.drawLine(board.getX(5), board.getY(5), board.getX(3), board.getY(3), blurPaint);
        c.drawLine(board.getX(18), board.getY(18), board.getX(3), board.getY(3), blurPaint);
        c.drawLine(board.getX(18), board.getY(18), board.getX(20), board.getY(20), blurPaint);

        c.drawLine(board.getX(6), board.getY(6), board.getX(8), board.getY(8), blurPaint);
        c.drawLine(board.getX(6), board.getY(6), board.getX(15), board.getY(15), blurPaint);
        c.drawLine(board.getX(17), board.getY(17), board.getX(15), board.getY(15), blurPaint);
        c.drawLine(board.getX(17), board.getY(17), board.getX(8), board.getY(8), blurPaint);

        c.drawLine(board.getX(1), board.getY(1), board.getX(7), board.getY(7), blurPaint);
        c.drawLine(board.getX(11), board.getY(11), board.getX(9), board.getY(9), blurPaint);
        c.drawLine(board.getX(16), board.getY(16), board.getX(22), board.getY(22), blurPaint);
        c.drawLine(board.getX(12), board.getY(12), board.getX(14), board.getY(14), blurPaint);


        for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; ++i) {
            c.drawCircle(board.getX(i), board.getY(i),10f,finalPaint);
           // c.drawBitmap(bitmap, board.getX(i) - wt, board.getY(i) - ht , null);
        }

        Actor[] actors1 = game.getPlayer().getActors();
        for (Actor actor : actors1) {
            c.drawBitmap(bitmap, actor.getPosx() - wt, actor.getPosy() - ht , null);
        }

        Actor[] actors2 = game.getOpponent().getActors();
        for (Actor actor : actors2) {
            c.drawBitmap(bitmap2, actor.getPosx() - wt, actor.getPosy() - ht , null);
        }

       // c.drawText("TIME  " + Integer.toString(min / 10) + Integer.toString(min % 10) + ":" + Integer.toString(sec / 10) + Integer.toString(sec % 10), 25, 55, textPaint);

        h.postDelayed(r, FRAME_RATE);
    }
    public void stopHandler(){
        h.removeCallbacks(r);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public NetworkGame getGame() {
        return game;
    }

    public void setGame(NetworkGame game) {
        this.game = game;
        this.board = game.getGameBoard();
    }

}
