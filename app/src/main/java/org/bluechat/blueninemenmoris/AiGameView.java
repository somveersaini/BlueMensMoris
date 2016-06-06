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
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.bluechat.blueninemenmoris.model.AIPlayer;
import org.bluechat.blueninemenmoris.model.Actor;
import org.bluechat.blueninemenmoris.model.Board;
import org.bluechat.blueninemenmoris.model.Game;
import org.bluechat.blueninemenmoris.model.GameException;
import org.bluechat.blueninemenmoris.model.HumanPlayer;
import org.bluechat.blueninemenmoris.model.LocalGame;
import org.bluechat.blueninemenmoris.model.MinimaxAIPlayer;
import org.bluechat.blueninemenmoris.model.Move;
import org.bluechat.blueninemenmoris.model.Player;
import org.bluechat.blueninemenmoris.model.Token;

import java.util.jar.Attributes;

public class AiGameView extends View {


    public int time = 0;
    public static boolean focus = false;
    private Handler h;
    private int selectednode;
    private int starttime = 0, currenttime = 0;
    Actor currActor = null;

    private VelocityTracker mVelocityTracker = null;
    private int offsetX;
    private int offsetY;

    private Paint testPaint;
    private Paint textPaint;                // for painting the text
    private Paint finalPaint;
    private Paint blurPaint;
    private int viewHeight;
    private int viewWidth;
    private int squareSpace;
    private int removedx; // for x of removed piece
    private int removedSpace; //for spacing the pieces
    private int squareStart; //from where square start
    private Board board;     //gameboard
    private LocalGame game;   //localgame
    private boolean madeamill = false;

    public static final int MAX_MOVES = 150;
    public static int totalMoves = 0;
    private int numberGames = 1, fixedNumberGames = 1, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;
    private  long gamesStart;
    private int removedPieceP1, removedPieceP2;

    private BitmapDrawable graphic;
    Bitmap bitmap, bitmap2;
    int wt;
    int ht;
    Player p1,p2;

    private final int FRAME_RATE = 30;
    private int starttimeinsec = 0;

    private Context context;
    private Typeface typeface;
    private Typeface typeface1;


    public AiGameView(Context context, AttributeSet attrs) throws GameException {
        super(context, attrs);
        this.context = context;

        game = new LocalGame();
        p1 = new HumanPlayer("sam", Token.PLAYER_1,9);
        p2 = new MinimaxAIPlayer(Token.PLAYER_2,9,3);
        game.setPlayers(p1,p2);
        board = game.getGameBoard();

        gamesStart = System.nanoTime();
        h = new Handler();
        starttimeinsec = (int) System.currentTimeMillis() / 1000;

        graphic = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bdots32);
        bitmap = graphic.getBitmap();
        graphic = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bopp32);
        bitmap2 = graphic.getBitmap();

        typeface = Typeface.createFromAsset(context.getAssets(),
                "Gasalt-Black.ttf");
        typeface1 = Typeface.createFromAsset(context.getAssets(),
                "future.otf");

        init();
    }

    public void init(){

        wt = bitmap.getWidth()/2;
        ht = bitmap.getHeight()/2;
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

//        switch (Settings.theme) {
//            case Constants.MAGNETA:
//                testPaint.setColor(Color.MAGENTA);
//                textPaint.setColor(Color.MAGENTA);
//                this.setBackgroundColor(Color.WHITE);
//                break;
//
//            case Constants.WHITE:
//                testPaint.setColor(Color.GRAY);
//                textPaint.setColor(Color.MAGENTA);
//                this.setBackgroundColor(Color.WHITE);
//                break;
//
//            case Constants.BLACK:
//                testPaint.setColor(Color.WHITE);
//                textPaint.setColor(Color.WHITE);
//                this.setBackgroundColor(Color.BLACK);
//                break;
//
//            case Constants.BLUE:
//                testPaint.setColor(Color.WHITE);
//                blurPaint.setColor(Color.BLUE);
//                textPaint.setColor(Color.WHITE);
//                this.setBackgroundColor(Color.parseColor("#2196fe"));
//                break;
//        }
    }
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
        Log.d("gameview", viewHeight +  " " + viewWidth);

        int  squareStartY =   (viewHeight - viewWidth)/2;


        squareStart = viewWidth/8;
        squareSpace = squareStartY;

        board.setPosXY(squareStart,squareStartY);

        int startPieceX = viewWidth/10;
        int startPieceY1 = squareStartY/2;
        int startPieceY2 = viewWidth + 3*startPieceY1;
        game.getPlayer1().setActors(startPieceX,startPieceY1);
        game.getPlayer2().setActors(startPieceX,startPieceY2);
        removedSpace = viewWidth/10;
        removedx  = bitmap.getWidth()/2 + 1;
        if(squareStart > 4*removedx){
            removedx = squareStart/2;
        }


    }

    private Runnable r = new Runnable() {

        public void run() {
            invalidate();
        }
    };


    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);


        if (action == MotionEvent.ACTION_DOWN) {

            currenttime = (int) Math.abs(System.currentTimeMillis());
            currenttime = Math.abs(currenttime);
            // System.out.println(currenttime + " " + starttime);
            if (Math.abs(currenttime - starttime) < 300) {
                // showsolution();
            }
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }
            mVelocityTracker.addMovement(event);

            int y = (int) event.getY();
            int x = (int) event.getX();

            int min = 10000;


            if (madeamill) {
                //Token opponentPlayer = (game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;

                Actor[] actorscurrent = game.getOpponentPlayer().getActors();
                for (Actor actor : actorscurrent) {
                    if(!actor.isRemoved()) {
                        int t1 = y - (actor.getPosy());
                        int t2 = x - (actor.getPosx());
                        int temp = (int) Math.sqrt(Math.abs(t1 * t1 + t2 * t2));
                        if (temp < min) {
                            min = temp;
                            offsetY = t1;
                            offsetX = t2;
                            currActor = actor;
                            actor.setAvailableToRemove(true);
                        }
                    }
                }
            }else {
                Actor[] actorscurrent = game.getCurrentTurnPlayer().getActors();
                for (Actor actor : actorscurrent) {
                    if(game.getCurrentGamePhase() == Game.PLACING_PHASE) {
                        if (!actor.isRemoved() && !actor.isPlaced()) {
                            int t1 = y - (actor.getPosy());
                            int t2 = x - (actor.getPosx());
                            int temp = (int) Math.sqrt(Math.abs(t1 * t1 + t2 * t2));
                            if (temp < min) {
                                min = temp;
                                offsetY = t1;
                                offsetX = t2;
                                currActor = actor;
                            }
                        }
                    }else {
                        if (!actor.isRemoved()) {
                            int t1 = y - (actor.getPosy());
                            int t2 = x - (actor.getPosx());
                            int temp = (int) Math.sqrt(Math.abs(t1 * t1 + t2 * t2));
                            if (temp < min) {
                                min = temp;
                                offsetY = t1;
                                offsetX = t2;
                                currActor = actor;
                            }
                        }
                    }
                }
            }
            Log.d("currentmin", " " + min);
            if (min > 80 ) {
                currActor = null;
            }else {
                //  Log.d("selected opponent piece", " " + mini);
            }
            starttime = currenttime;

        } else if (action == MotionEvent.ACTION_MOVE) {
            mVelocityTracker.addMovement(event);
            mVelocityTracker.computeCurrentVelocity(1000);
            int xvel = (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
            int yvel = (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

            int vel = (int) Math.sqrt(xvel * xvel + yvel * yvel);

            int y = (int) event.getY();
            int x = (int) event.getX();
            // Log.d("moving", x + " " + y);
            if (currActor != null ) {
                // Log.d("moving", "curractor");
                currActor.setPosxy(x - offsetX , y - offsetY);
                // GameView.myvertex[selectednode].x = x - nodewidth / 2;
                // GameView.myvertex[selectednode].y = y - nodeheight / 2;

            }

        } else if (action == MotionEvent.ACTION_UP) {
            Log.d("action", "up");
            int min = 1000;
            int mini = -1;
            if(currActor != null) {
                if(madeamill){
                    mini = currActor.getPlacedIndex();
                    min = 1;
                }
                else {
                    for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
                        try {
                            if (board.positionIsAvailable(i)) {
                                int t1 = board.getY(i) - (currActor.getPosy());
                                int t2 = board.getX(i) - (currActor.getPosx());
                                int temp = (int) Math.sqrt(Math.abs(t1 * t1 + t2 * t2));
                                if (temp < min) {
                                    min = temp;
                                    mini = i;
                                }
                            }
                        } catch (GameException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Player p = game.getCurrentTurnPlayer();
                int boardIndex;
                if (min < 80 && mini != -1) {
                    Log.d("current game phase", "  ->  " +game.getCurrentGamePhase());
                    boardIndex = mini;
                    if(game.getCurrentGamePhase() == Game.PLACING_PHASE) {
                        Log.d("placing phase", "onTouchEvent: removing");
                        try {
                            if(madeamill){
                                Log.d("removing at pos", ""+ mini);
                                Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                                if (game.removePiece(boardIndex, opponentPlayer)) {
                                    game.updateCurrentTurnPlayer();
                                    System.out.println("removed piece at " + boardIndex);

                                    if(opponentPlayer == Token.PLAYER_1){
                                        ++removedPieceP1;
                                        currActor.setPosxy(removedx, (squareSpace) + ((removedPieceP1 + 1) * removedSpace));
                                        Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                    }else {
                                        ++removedPieceP2;
                                        currActor.setPosxy((viewWidth - removedx), (viewWidth + squareSpace) - (removedPieceP2 * removedSpace));
                                        Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                    }

                                    currActor.setRemoved(true);
                                    madeamill = false;
                                    aiPlay();
                                } else {
                                    System.out.println("You can't remove a piece from there. Try again");
                                }
                            } else {
                                if (game.placePieceOfPlayer(boardIndex, p.getPlayerToken())) {
                                    Log.d("selected", "pos " + mini);
                                    numberMoves++; // TODO testing
                                    totalMoves++;
                                    p.raiseNumPiecesOnBoard();
                                    currActor.setPosxy(board.getX(mini), board.getY(mini));
                                    currActor.setPlacedIndex(mini);

                                    if (game.madeAMill(boardIndex, p.getPlayerToken())) {
                                        madeamill = true;
                                        System.out.println("You made a mill. You can remove a piece of your oponent: ");
                                    }
                                    else {
                                        System.out.println("changed current Player");
                                        game.updateCurrentTurnPlayer();
                                        aiPlay();
                                    }
                                } else {
                                    System.out.println("You can't place a piece there. Try again");
                                }
                            }
                        } catch (GameException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        System.out.println("The pieces are all placed. Starting the fun part... ");
                        try {
                            if(!game.isTheGameOver() && numberMoves < MAX_MOVES) {
                                if(madeamill){
                                    boardIndex = currActor.getPlacedIndex();
                                    Log.d("removing at pos", ""+ mini);
                                    Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                                    if (game.removePiece(boardIndex, opponentPlayer)) {
                                        game.updateCurrentTurnPlayer();
                                        System.out.println("removed piece at " + boardIndex);

                                        if(opponentPlayer == Token.PLAYER_1){
                                            ++removedPieceP1;
                                            currActor.setPosxy(removedx, (squareSpace) + (removedPieceP1 * removedSpace));
                                            Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + (removedPieceP1 * removedSpace));

                                        }else {
                                            ++removedPieceP2;
                                            currActor.setPosxy((viewWidth - removedx), (viewWidth + squareSpace) - (removedPieceP2 * removedSpace));
                                            Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                        }

                                        currActor.setRemoved(true);
                                        madeamill = false;
                                        aiPlay();
                                    } else {
                                        System.out.println("You can't remove a piece from there. Try again");
                                    }
                                }
                                else {
                                    int srcIndex, destIndex;
                                    srcIndex = currActor.getPlacedIndex();
                                    destIndex = mini;
                                    System.out.println("Move piece from "+srcIndex+" to "+destIndex);

                                    int result;
                                    if((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken())) == Game.VALID_MOVE) {
                                        numberMoves++; // TODO testing
                                        totalMoves++;
                                        currActor.setPosxy(board.getX(mini), board.getY(mini));
                                        currActor.setPlacedIndex(mini);
                                        if(game.madeAMill(destIndex, p.getPlayerToken())) {
                                            madeamill = true;
                                        }else {
                                            System.out.println("changed current Player");
                                            game.updateCurrentTurnPlayer();
                                            if(!game.isTheGameOver()) {
                                                aiPlay();
                                            }
                                        }
                                    } else {
                                        currActor.setPosxy(board.getX(srcIndex), board.getY(srcIndex));
                                        System.out.println("Invalid move. Error code: "+result);
                                    }
                                }
                            }
                            if(game.isTheGameOver() || numberMoves >= MAX_MOVES) {
                                String finishLine;
                                String finishDesc;
                                if(!game.isTheGameOver()) {
                                    System.out.println("Draw!");
                                    draws++;
                                    finishLine = "Game Draw";
                                    finishDesc = "Opps!!\n No one wins\ncurrunt game is A draw.\n" +
                                            "\n" +
                                            " Would you like to play a new game";
                                    showDialog(finishLine, finishDesc);
                                } else {
                                    if((game).getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1) {
                                        p1Wins++;
                                        finishLine = game.getCurrentTurnPlayer().getName() + " Win!!";
                                    } else {
                                        p2Wins++;
                                        finishLine = game.getOpponentPlayer().getName() + " Win!!";
                                    }
                                    finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game";
                                    showDialog(finishLine, finishDesc);

                                }
                                numberMoves = 0;
                                game = new LocalGame();
                                p1.reset();
                                p2.reset();
                                game.setPlayers(p1, p2);
                                this.invalidate();
                                return true;
                            }
                        }catch (GameException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if(currActor.getPlacedIndex() == -1){
                        currActor.setToPreviousPosition();
                    }
                    else {
                        currActor.setPosxy(board.getX(currActor.getPlacedIndex()),board.getY(currActor.getPlacedIndex()));
                    }
                }
            }
            else{
                currActor = null;
            }
          //  aiPlay();
        }

        return true;
    }

    public void aiPlay() throws GameException{
        MinimaxAIPlayer p = (MinimaxAIPlayer) game.getPlayer2();
        int boardIndex;
        Actor rActor;
        if(game.getCurrentGamePhase() == Game.PLACING_PHASE){
            boardIndex = p.getIndexToPlacePiece(board);


            Actor[] actors = p.getActors();
            for (Actor actor : actors) {
                if(!actor.isPlaced()){
                    numberMoves++; // TODO testing
                    totalMoves++;
                    p.raiseNumPiecesOnBoard();
                    actor.setPosxy(board.getX(boardIndex), board.getY(boardIndex));
                    actor.setPlacedIndex(boardIndex);
                    break;
                }
            }

            if(game.placePieceOfPlayer(boardIndex, p.getPlayerToken())) {

                if(game.madeAMill(boardIndex, p.getPlayerToken())) {
                    Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                    boardIndex = p.getIndexToRemovePieceOfOpponent(board);
                    game.removePiece(boardIndex, opponentPlayer);
                    rActor = game.getPlayer1().getActorAt(boardIndex);
                    ++removedPieceP1;
                    rActor.setPosxy(removedx, (squareSpace) + (removedPieceP1 * removedSpace));
                    rActor.setRemoved(true);
                }
                game.updateCurrentTurnPlayer();
            } else {
                System.out.println("You can't place a piece there. Try again");
            }

        }else{
            int srcIndex, destIndex;
            Move move = p.getPieceMove(board, game.getCurrentGamePhase());
            srcIndex = move.srcIndex;
            destIndex = move.destIndex;
            System.out.println("Move piece from "+srcIndex+" to "+destIndex);

            int result;
            if((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken())) == Game.VALID_MOVE) {
                numberMoves++; // TODO testing
                totalMoves++;
                rActor = p.getActorAt(srcIndex);
                rActor.setPosxy(board.getX(destIndex), board.getY(destIndex));
                rActor.setPlacedIndex(destIndex);
                if(game.madeAMill(destIndex, p.getPlayerToken())) {
                    Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                    boardIndex = p.getIndexToRemovePieceOfOpponent(board);
                    game.removePiece(boardIndex, opponentPlayer);
                    rActor = game.getPlayer1().getActorAt(boardIndex);
                    ++removedPieceP1;
                    rActor.setPosxy(removedx, (squareSpace) + (removedPieceP1 * removedSpace));
                    rActor.setRemoved(true);
                }
                game.updateCurrentTurnPlayer();
            }
            if(game.isTheGameOver() || numberMoves >= MAX_MOVES){
                System.out.println(game.isTheGameOver() + " " + numberMoves);
                String finishLine;
                String finishDesc;
                if(!game.isTheGameOver()) {
                    System.out.println("Draw!");
                    draws++;
                    finishLine = "Game Draw";
                    finishDesc = "Opps!!\n No one wins\ncurrunt game is A draw.\n" +
                            "\n" +
                            " Would you like to play a new game";
                    showDialog(finishLine, finishDesc);
                } else {
                    System.out.println("Game over. Player "+ game.getOpponentPlayer().getPlayerToken()+" Won");
                    finishLine = "Android Win!! ";
                    finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game";
                    showDialog(finishLine, finishDesc);
                }
                numberMoves = 0;
            }
        }

    }
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

        Actor[] actors1 = game.getPlayer1().getActors();
        for (Actor actor : actors1) {
            c.drawBitmap(bitmap, actor.getPosx() - wt, actor.getPosy() - ht , null);
        }

        Actor[] actors2 = game.getPlayer2().getActors();
        for (Actor actor : actors2) {
            c.drawBitmap(bitmap2, actor.getPosx() - wt, actor.getPosy() - ht , null);
        }





        c.drawText("TIME  " + Integer.toString(min / 10) + Integer.toString(min % 10) + ":" + Integer.toString(sec / 10) + Integer.toString(sec % 10), 25, 55, textPaint);

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

    public LocalGame getGame() {
        return game;
    }

    public void setGame(LocalGame game) {
        this.game = game;
    }
    public void showDialog(String finishline , String finishdesc){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.finished, null);
        TextView tv1 = (TextView) view.findViewById(R.id.gamename);
        tv1.setTypeface(typeface1);
        TextView line = (TextView) view.findViewById(R.id.finishline);
        line.setTypeface(typeface);
        line.setText(finishline);
        TextView desc = (TextView) view.findViewById(R.id.finishdescription);
        desc.setTypeface(typeface);
        desc.setText(finishdesc);
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button finishnewgame = (Button) view.findViewById(R.id.finishnewgame);
        finishnewgame.setTypeface(typeface);

        finishnewgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the achiewments

                alertDialog.cancel();
            }
        });
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();
    }
}
