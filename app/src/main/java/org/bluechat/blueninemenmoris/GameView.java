package org.bluechat.blueninemenmoris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import org.bluechat.blueninemenmoris.model.Player;
import org.bluechat.blueninemenmoris.model.Token;

public class GameView extends View {

    private Paint finalPaint, blurPaint;  //painting
    private int viewHeight, viewWidth, wt, ht;
    private int squareSpace, squareStartY, squareStartX; //for board drawing
    private int startPieceX, startPieceY1, startPieceY2;
    private int removedX; // for x of removed piece
    private int removedSpace; //for spacing the pieces

    private LocalGame game;   //game
    private Board board;     //gameBoard
    private BitmapDrawable graphic;
    private Bitmap bitmap, bitmap2;   //stone bitmaps

    private Handler h;
    private Runnable r = new Runnable() {

        public void run() {
            invalidate();
        }
    };

    public GameView(Context context, AttributeSet attrs) throws GameException {
        super(context, attrs);

        game = new LocalGame();
        Player p1 = new HumanPlayer("sam", Token.PLAYER_1,9);
        Player p2 = new HumanPlayer("aac", Token.PLAYER_2,9);
        game.setPlayers(p1,p2);
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
        h = new Handler();

        finalPaint = new Paint();
        finalPaint.setAntiAlias(true);
        finalPaint.setStrokeWidth(5);
        finalPaint.setStyle(Paint.Style.FILL);

        blurPaint = new Paint();
        blurPaint.setAntiAlias(true);
        blurPaint.setAlpha(140);
        blurPaint.setStyle(Paint.Style.STROKE);
        blurPaint.setColor(Color.parseColor("#2196fe"));
        blurPaint.setStrokeWidth(6);

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;

        Log.d("gameview", viewHeight +  " " + viewWidth);

        squareStartY =   (viewHeight - viewWidth)/2;
        squareStartX = viewWidth / 8;
        board.setPosXY(squareStartX,squareStartY);


        squareSpace = squareStartY;

        startPieceX = viewWidth/10;
        startPieceY1 = squareStartY;
        startPieceY2 = viewWidth + startPieceY1;
        game.getPlayer1().setActors(startPieceX,startPieceY1);
        game.getPlayer2().setActors(startPieceX,startPieceY2);

        removedSpace = viewWidth/10;
        removedX = bitmap.getWidth()/2 + 1;
        if(squareStartX > 4* removedX){
            removedX = squareStartX /2;
        }


    }

    public  int getP1rx(){
        return removedX;
    }

    public  int getP2rx(){
        return viewWidth - removedX;
    }

    public int getP1ry(int removedPieceP1){
        return (squareSpace) + ((removedPieceP1 + 1) * removedSpace);
    }

    public int getP2ry(int removedPieceP2){
        return (viewWidth + squareSpace) - (removedPieceP2 * removedSpace);
    }

    protected void onDraw(Canvas c) {

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
        }

        Actor[] actors1 = game.getPlayer1().getActors();
        for (Actor actor : actors1) {
            if(actor != null) {
                c.drawBitmap(bitmap, actor.getPosx() - wt, actor.getPosy() - ht, null);
            }
        }

        Actor[] actors2 = game.getPlayer2().getActors();
        for (Actor actor : actors2) {
            if(actor != null) {
                c.drawBitmap(bitmap2, actor.getPosx() - wt, actor.getPosy() - ht, null);
            }
        }

        h.postDelayed(r, 20);
    }
    public void stopHandler(){
        h.removeCallbacksAndMessages(null);
    }

    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }

    public int getViewHeight() {
        return viewHeight;
    }
    public int getViewWidth() {
        return viewWidth;
    }

    public int getStartPieceX() {
        return startPieceX;
    }
    public int getStartPieceY1() {
        return startPieceY1;
    }
    public int getStartPieceY2 (){
        return startPieceY2;
    }

    public int getSquareStartX() {
        return squareStartX;
    }
    public int getSquareStartY() {
        return squareStartY;
    }

    public LocalGame getGame() {
        return game;
    }

    public void setGame(LocalGame game) {
        this.game = game;
        this.board = game.getGameBoard();
    }
}
