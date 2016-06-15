package org.bluechat.blueninemenmoris;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    public static final int MAX_MOVES = 150;
    public static int totalMoves = 0;
    public LocalGame game;
    Actor currActor = null;
    Typeface typeface, typeface1;
    Player p1, p2;
    Actor rActor;
    Actor actionActor;
    Handler handler;
    long HANDLER_DELAY = 10;
    TextView top, topdesc;
    TextView bottom, bottomdesc;
    private Board board;
    private GameView gameView;
    private int numberGames = 1, fixedNumberGames = 1, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;
    private  long gamesStart;
    private int removedPieceP1, removedPieceP2;
    private int starttime = 0, currenttime = 0;
    private VelocityTracker mVelocityTracker = null;
    private boolean madeamill = false;
    private int offsetX;
    private int offsetY;
    View.OnTouchListener gameListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);



            if (action == MotionEvent.ACTION_DOWN) {

                currenttime = (int) Math.abs(System.currentTimeMillis());
                currenttime = Math.abs(currenttime);

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
                        if (!actor.isRemoved()) {
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
                } else {
                    Actor[] actorscurrent = game.getCurrentTurnPlayer().getActors();
                    for (Actor actor : actorscurrent) {
                        if (game.getCurrentGamePhase() == Game.PLACING_PHASE) {
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
                        } else {
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
                if (min > 80) {
                    currActor = null;
                } else {
                    Settings.selectSound();
                    if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                        topdesc.setText("Playing...");
                        bottomdesc.setText("Waiting");
                    }else {
                        bottomdesc.setText("Playing...");
                        topdesc.setText("Waiting");
                    }
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
                if (currActor != null) {
                    currActor.setPosxy(x - offsetX, y - offsetY);
                }

            } else if (action == MotionEvent.ACTION_UP) {
                Log.d("action", "up");
                int min = 1000;
                int mini = -1;
                if (currActor != null) {
                    if (madeamill) {
                        mini = currActor.getPlacedIndex();
                        min = 1;
                    } else {
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
                        Log.d("current game phase", "  ->  " + game.getCurrentGamePhase());
                        boardIndex = mini;
                        if (game.getCurrentGamePhase() == Game.PLACING_PHASE) {
                            // Log.d("placing phase", "onTouchEvent: removing");
                            try {
                                if (madeamill) {
                                    // Log.d("removing at pos", ""+ mini);
                                    Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                                    if (game.removePiece(boardIndex, opponentPlayer)) {
                                        System.out.println("removed piece at " + boardIndex);
                                        if (opponentPlayer == Token.PLAYER_1) {
                                            ++removedPieceP1;
                                            currActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                                            //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                        } else {
                                            ++removedPieceP2;
                                            currActor.setPosxy(gameView.getP2rx(), gameView.getP2ry(removedPieceP2));
                                            //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                        }

                                        Settings.removeSound();

                                        currActor.setRemoved(true);
                                        madeamill = false;
                                        game.updateCurrentTurnPlayer();
                                        if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                                            topdesc.setText("Playing...");
                                            bottomdesc.setText("Waiting");
                                        }else {
                                            bottomdesc.setText("Playing...");
                                            topdesc.setText("Waiting");
                                        }
                                        if (game.getCurrentTurnPlayer().isAI()) {
                                            new Asyncaiplay().execute();
                                        }
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
                                            Settings.millSound();
                                            if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                                                topdesc.setText("Mill!! Choose " + p2.getName() + "'s stone to remove");
                                                bottomdesc.setText("Watching..");
                                            }else {
                                                bottomdesc.setText("Mill!! Choose " + p1.getName() +"'s stone to remove");
                                                topdesc.setText("Watching..");
                                            }
                                            System.out.println("You made a mill. You can remove a piece of your oponent: ");
                                        } else {
                                            System.out.println("changed current Player");
                                            game.updateCurrentTurnPlayer();
                                            Settings.placeSound();
                                            if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                                                topdesc.setText("Your turn");
                                                bottomdesc.setText("Waiting..");
                                            }else {
                                                bottomdesc.setText("Your turn");
                                                topdesc.setText("Waiting..");
                                            }

                                            if (game.getCurrentTurnPlayer().isAI()) {
                                                new Asyncaiplay().execute();
                                            }
                                        }
                                    } else {
                                        System.out.println("You can't place a piece there. Try again");
                                    }
                                }
                            } catch (GameException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // System.out.println("The pieces are all placed. Starting the fun part... ");
                            try {
                                if (!game.isTheGameOver() && numberMoves < MAX_MOVES) {
                                    if (madeamill) {
                                        boardIndex = currActor.getPlacedIndex();
                                        // Log.d("removing at pos", ""+ mini);
                                        Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                                        if (game.removePiece(boardIndex, opponentPlayer)) {
                                            System.out.println("removed piece at " + boardIndex);
                                            if (opponentPlayer == Token.PLAYER_1) {
                                                ++removedPieceP1;
                                                currActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                                                //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                            } else {
                                                ++removedPieceP2;
                                                currActor.setPosxy(gameView.getP2rx(), gameView.getP2ry(removedPieceP2));
                                                //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                            }

                                            currActor.setRemoved(true);
                                            madeamill = false;

                                            game.updateCurrentTurnPlayer();
                                            Settings.removeSound();
                                            if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                                                topdesc.setText("Your turn!!");
                                                bottomdesc.setText("Waiting...");
                                            }else {
                                                bottomdesc.setText("Your turn!!");
                                                topdesc.setText("Waiting...");
                                            }
                                            if (game.getCurrentTurnPlayer().isAI()) {
                                                new Asyncaiplay().execute();
                                            }
                                        } else {
                                            System.out.println("You can't remove a piece from there. Try again");
                                        }
                                    } else {
                                        int srcIndex, destIndex;
                                        srcIndex = currActor.getPlacedIndex();
                                        destIndex = mini;
                                        System.out.println("Move piece from " + srcIndex + " to " + destIndex);

                                        int result;
                                        if ((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken())) == Game.VALID_MOVE) {
                                            numberMoves++; // TODO testing
                                            totalMoves++;
                                            currActor.setPosxy(board.getX(mini), board.getY(mini));
                                            currActor.setPlacedIndex(mini);
                                            if (game.madeAMill(destIndex, p.getPlayerToken())) {
                                                madeamill = true;

                                                Settings.millSound();
                                                if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                                                    topdesc.setText("Mill!! Choose " + p2.getName() + "'s stone to remove");
                                                    bottomdesc.setText("Watching..");
                                                }else {
                                                    bottomdesc.setText("Mill!! Choose " + p1.getName() +"'s stone to remove");
                                                    topdesc.setText("Watching..");
                                                }

                                            } else {
                                                game.updateCurrentTurnPlayer();
                                                System.out.println("changed current Player");
                                                Settings.place1Sound();
                                                if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                                                    topdesc.setText("Your turn");
                                                    bottomdesc.setText("Waiting...");
                                                }else {
                                                    bottomdesc.setText("Your turn");
                                                    topdesc.setText("Waiting...");
                                                }
                                                if (game.getCurrentTurnPlayer().isAI()) {
                                                    new Asyncaiplay().execute();
                                                }
                                            }
                                        } else {
                                            currActor.setPosxy(board.getX(srcIndex), board.getY(srcIndex));
                                            System.out.println("Invalid move. Error code: " + result);
                                        }
                                    }
                                }
                                if (game.isTheGameOver() || numberMoves >= MAX_MOVES) {
                                    String finishLine;
                                    String finishDesc;
                                    if (!game.isTheGameOver()) {
                                        System.out.println("Draw!");
                                        draws++;
                                        finishLine = "Game Draw";
                                        finishDesc = "Opps!!\n No one wins\ncurrunt game is A draw.\n" +
                                                "\n" +
                                                " Would you like to play a new game";
                                        showDialog(finishLine, finishDesc);
                                    } else {
                                        Settings.winSound();
                                        System.out.println("Game over. Player " + game.getOpponentPlayer().getPlayerToken() + " Won");
                                        if ((game).getOpponentPlayer().getPlayerToken() == Token.PLAYER_1) {
                                            p1Wins++;
                                            finishLine = game.getPlayer1().getName() + " Win!!";
                                        } else {
                                            p2Wins++;
                                            finishLine = game.getPlayer2().getName() + " Win!!";
                                        }
                                        finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game";
                                        showDialog(finishLine, finishDesc);
                                    }
                                    numberMoves = 0;
                                    game = new LocalGame();
                                    p1.reset();
                                    p2.reset();
                                    game.setPlayers(p1, p2);
                                }
                            } catch (GameException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (currActor.getPlacedIndex() == -1) {
                            currActor.setToPreviousPosition();
                        } else {
                            currActor.setPosxy(board.getX(currActor.getPlacedIndex()), board.getY(currActor.getPlacedIndex()));
                        }
                        Settings.moveSound();
                        if(game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1){
                            topdesc.setText("Your Turn..");
                            bottomdesc.setText("Waiting...");
                        }else {
                            bottomdesc.setText("Your turn..");
                            topdesc.setText("Waiting...");
                        }
                    }
                } else {
                    currActor = null;
                }

            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeface = Typeface.createFromAsset(getAssets(),
                "CarterOne.ttf");
        typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");

        handler = new Handler();
        gameView = (GameView)findViewById(R.id.gameView);

       init();

    }

    public void init(){
        top = (TextView) findViewById(R.id.top);
        bottom = (TextView) findViewById(R.id.bottom);
        topdesc = (TextView) findViewById(R.id.topdesc);
        bottomdesc = (TextView) findViewById(R.id.bottomdesc);
        top.setTypeface(typeface);
        topdesc.setTypeface(typeface);
        bottom.setTypeface(typeface);
        bottomdesc.setTypeface(typeface);

        gameView.running = false;
        gameView.stopHandler();
        Settings.load(getApplicationContext());

        try {
            game = new LocalGame();
            p1 = new HumanPlayer(Settings.pName, Token.PLAYER_1,9);
            if(getIntent().getBooleanExtra("isAI",false)){
                int depth = getIntent().getIntExtra("level", 3);
                p2 = new MinimaxAIPlayer(Token.PLAYER_2, 9, depth);
            }else {
                p2 = new HumanPlayer("Opponent", Token.PLAYER_2, 9);
            }
            game.setPlayers(p1,p2);
            board = game.getGameBoard();
            gameView.setGame(game);
        } catch (GameException e) {
            e.printStackTrace();
        }
        gameView.running = true;
        refresh();
    }
    public void refresh(){
        p1.setName(Settings.pName);
        top.setText(p1.getName());
        bottom.setText(p2.getName());
        topdesc.setText("Your turn");
        bottomdesc.setText("Waiting..");
        gameView.setOnTouchListener(gameListner);
        gameView.invalidate();
    }
    public void aiPlay(){
        try {
            MinimaxAIPlayer p = (MinimaxAIPlayer) game.getPlayer2();
            int boardIndex;

            if(game.getCurrentGamePhase() == Game.PLACING_PHASE){
                boardIndex = p.getIndexToPlacePiece(board);

                Actor[] actors = p.getActors();
                for (Actor actor : actors) {
                    if (!actor.isPlaced() && !actor.isRemoved()) {
                        numberMoves++; // TODO testing
                        totalMoves++;
                        p.raiseNumPiecesOnBoard();
                        int x2 = board.getX(boardIndex);
                        int x1 = actor.getPosx();

                        int y2 = board.getY(boardIndex);
                        int y1 = actor.getPosy();

                        int pS;
                        int px = (x2 - x1);
                        int py = (y2 - y1);
                        if(Math.abs(px)  > Math.abs(py)){
                            pS = Math.abs(py);
                        }
                        else {
                            pS = Math.abs(px);
                        }
                        if (pS == 0) {
                            pS = 1;
                        }
                        float ySlope = py/pS;
                        float xSlope = px/pS;
                        actionActor = actor;
                        for (int i = 1; i <= pS; i++) {
                           // actor.setPosxy(x1 + i*xSlope, (int) (y1 + i*ySlope));
                            Log.d("cordinates ","x : "+ (x1+i*xSlope) +"   y: "+(y1 + i*ySlope));
                            actor.setPosxy((int) (x1 + i * xSlope), (int) (y1 + i * ySlope));
                        }

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
                        rActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                        rActor.setRemoved(true);
                    }
                    game.updateCurrentTurnPlayer();
                } else {
                    System.out.println("You can't place a piece there. Try again");
                }

            }else{
                int srcIndex, destIndex;
                Move move = p.getPieceMove(board, game.getCurrentGamePhase());
                if(move != null) {
                    srcIndex = move.srcIndex;
                    destIndex = move.destIndex;
                    System.out.println("Move piece from " + srcIndex + " to " + destIndex);

                    int result;
                    if ((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken())) == Game.VALID_MOVE) {
                        numberMoves++; // TODO testing
                        totalMoves++;
                        rActor = p.getActorAt(srcIndex);
                        rActor.setPosxy(board.getX(destIndex), board.getY(destIndex));
                        rActor.setPlacedIndex(destIndex);
                        if (game.madeAMill(destIndex, p.getPlayerToken())) {
                            Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                            boardIndex = p.getIndexToRemovePieceOfOpponent(board);
                            game.removePiece(boardIndex, opponentPlayer);
                            rActor = game.getPlayer1().getActorAt(boardIndex);
                            ++removedPieceP1;
                            rActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                            rActor.setRemoved(true);
                        }
                        game.updateCurrentTurnPlayer();
                    }
                }else {
                    numberMoves = MAX_MOVES;
                }
                if(game.isTheGameOver() || numberMoves >= MAX_MOVES){
                    System.out.println(game.isTheGameOver() + " " + numberMoves);

                    if (!game.isTheGameOver() && numberMoves > MAX_MOVES) {
                        System.out.println("Draw!");
                        draws++;
                        final String finishLine = "Game Draw";
                        final String finishDesc = "Opps!!\n No one wins\ncurrent game is a draw.\n" +
                                "\n" +
                                " Would you like to play a new game";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(finishLine, finishDesc);
                            }
                        });

                    } else {
                        System.out.println("Game over. Player "+ game.getOpponentPlayer().getPlayerToken()+" Won");
                        final String finishLine = game.getOpponentPlayer().getName() + " Win!! ";
                        final String finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(finishLine, finishDesc);
                            }
                        });
                    }
                    numberMoves = 0;
                }
            }
        } catch (GameException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(String finishline , String finishdesc){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button finishnewgame = (Button) view.findViewById(R.id.finishnewgame);
        finishnewgame.setTypeface(typeface);

        alertDialog.setCancelable(false);

        finishnewgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the achiewments
                gameView.stopHandler();
                init();
                alertDialog.cancel();
            }
        });
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();
    }

    public void options(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.opt, null);
        TextView tv1 = (TextView) view.findViewById(R.id.gamename);
        tv1.setTypeface(typeface1);

        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button finishnewgame = (Button) view.findViewById(R.id.finishnewgame);
        finishnewgame.setTypeface(typeface);
        Button settings = (Button) view.findViewById(R.id.set);
        settings.setTypeface(typeface);

        finishnewgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the achiewments
                gameView.stopHandler();
                init();
                alertDialog.cancel();
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
                alertDialog.cancel();
            }
        });
        Button helps = (Button) view.findViewById(R.id.helps);
        helps.setTypeface(typeface);

        helps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                alertDialog.cancel();
            }
        });
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();
    }

    public void onResume(){
        super.onResume();
        refresh();
    }
    private class Asyncaiplay extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            topdesc.setText("Waiting..");
            bottomdesc.setText("Thinking..");
            gameView.setEnabled(false);
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            aiPlay();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //this method will be running on UI thread
            topdesc.setText("Your turn");
            bottomdesc.setText("Waiting..");
            gameView.setEnabled(true);
            gameView.invalidate();
        }
    }
}
