package org.bluechat.blueninemenmoris;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esotericsoftware.minlog.Log;

import org.bluechat.blueninemenmoris.model.Actor;
import org.bluechat.blueninemenmoris.model.Board;
import org.bluechat.blueninemenmoris.model.Game;
import org.bluechat.blueninemenmoris.model.GameException;
import org.bluechat.blueninemenmoris.model.HumanPlayer;
import org.bluechat.blueninemenmoris.model.Move;
import org.bluechat.blueninemenmoris.model.NetworkGame;
import org.bluechat.blueninemenmoris.model.Player;
import org.bluechat.blueninemenmoris.model.Token;
import org.bluechat.blueninemenmoris.net.GameClient;
import org.bluechat.blueninemenmoris.net.GameServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;



public class NetMainActivity extends AppCompatActivity {
    private Board board;
    private NetGameView gameView;
    public NetworkGame game;

    public static final int MAX_MOVES = 150;
    public static int totalMoves = 0;
    private int numberGames = 1, fixedNumberGames = 1, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;
    private  long gamesStart;
    private int removedPieceP1, removedPieceP2;

    private int starttime = 0, currenttime = 0;
    private VelocityTracker mVelocityTracker = null;

    Actor currActor = null;
    private boolean madeamill = false;

    Typeface typeface, typeface1;
    private int offsetX;
    private int offsetY;

    Player p1, p2;

    Handler handler;
    long HANDLER_DELAY = 100;

    GameServer gs = null;
    GameClient gc = null;

    Player p = null;
    int numberTries = 3;
    boolean firstTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_main);
        typeface = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");
        game = new NetworkGame();
        handler = new Handler();
        gameView = (NetGameView) findViewById(R.id.netGameView);
        setContentView(R.layout.connect);

        //network connect


//        try {
//            p1 = new HumanPlayer("sam", Token.PLAYER_1,9);
//            if(getIntent().getBooleanExtra("isAI",false)){
//                p2 = new MinimaxAIPlayer(Token.PLAYER_2, 9, 4);
//            }else {
//                p2 = new HumanPlayer("ashi", Token.PLAYER_2,9);
//            }
//           // game.setPlayers(p1,p2);
//            board = game.getGameBoard();
//            gameView.setGame(game);
//        } catch (GameException e) {
//            e.printStackTrace();
//        }
//
//        gameView.setOnTouchListener(gameListner);

    }

    View.OnTouchListener gameListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
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

                    Actor[] actorscurrent = game.getPlayer().getActors();
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
                    Actor[] actorscurrent = game.getPlayer().getActors();
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
                System.out.println("currentmin " + min);
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
                // Log.d("moving"w, x + " " + y);
                if (currActor != null ) {
                    currActor.setPosxy(x - offsetX , y - offsetY);
                }

            } else if (action == MotionEvent.ACTION_UP) {
                android.util.Log.d("action", "up");
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

                    Player p = game.getPlayer();
                    int boardIndex;
                    if (min < 80 && mini != -1) {
                        android.util.Log.d("current game phase", "  ->  " +game.getCurrentGamePhase());
                        boardIndex = mini;
                        if(game.getCurrentGamePhase() == Game.PLACING_PHASE) {
                            try {
                                if (game.isThisPlayerTurn()) {
                                    Player player = game.getPlayer();
                                    if(madeamill){
                                        // ask for the index of the opponent piece
                                        System.out.println("You made a mill. You can remove a piece of your oponent. Remove piece at: " + boardIndex);

                                        // validate removing with the connect
                                        if (gc.validatePieceRemoving(boardIndex)) {

                                            // validate removing locally
                                            if (game.removePiece(boardIndex, (player.getPlayerToken() == Token.PLAYER_1 ? Token.PLAYER_2 : Token.PLAYER_1))) {
                                                madeamill = false;
                                            } else {
                                                System.out.println("You can't remove a piece from there. Try again");
                                            }
                                        }
                                    }
                                    else {

                                        // update game with opponent move(s)
                                        ArrayList<Move> opponentMoves = gc.getOpponentMoves();
                                        game.updateGameWithOpponentMoves(opponentMoves);



                                        // ask user input
                                        System.out.println(player.getName() + " place piece on: " + boardIndex);

                                        // validate placing with the connect
                                        if (gc.validatePiecePlacing(boardIndex)) {

                                            // validate placing locally
                                            if (game.placePieceOfPlayer(boardIndex, player.getPlayerToken())) {

                                                if (game.madeAMill(boardIndex, player.getPlayerToken())) {
                                                    madeamill = true;
                                                }
                                                game.setTurn(false);
                                            }
                                        } else {
                                            System.out.println("The connect has considered that move invalid. Try again");
                                        }
                                    }
                                }
                            }catch (GameException e){
                                e.printStackTrace();
                            }
                            //Thread.sleep(10);
                            game.setTurn(gc.isThisPlayerTurn());
                            // check if the other player played the last piece of the placing phase
                            if (game.getCurrentGamePhase() != Game.PLACING_PHASE) {
                                // getting the right player to make the first move
                                if(game.playedFirst(gc.getPlayerThatPlaysFirst())) {
                                    game.setTurn(true);
                                }
                            }
                        }
                        else{
                            // System.out.println("The pieces are all placed. Starting the fun part... ");
                            try {
                                if(!game.isTheGameOver() && numberMoves < MAX_MOVES) {
                                    if(madeamill){
                                        boardIndex = currActor.getPlacedIndex();
                                        // Log.d("removing at pos", ""+ mini);
                                        Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                                        if (game.removePiece(boardIndex, opponentPlayer)) {
                                         //   game.updateCurrentTurnPlayer();
                                            System.out.println("removed piece at " + boardIndex);

                                            if(opponentPlayer == Token.PLAYER_1){
                                                ++removedPieceP1;
                                                currActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                                                //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                            }else {
                                                ++removedPieceP2;
                                                currActor.setPosxy(gameView.getP2rx(), gameView.getP2ry(removedPieceP2));
                                                //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                            }

                                            currActor.setRemoved(true);
                                            madeamill = false;


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
                                               // game.updateCurrentTurnPlayer();

                                            }
                                        } else {
                                            currActor.setPosxy(board.getX(srcIndex), board.getY(srcIndex));
                                            System.out.println("Invalid move. Error code: "+result);
                                        }
                                    }
                                }
                                if(game.isTheGameOver() || numberMoves >= MAX_MOVES){
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
                                        System.out.println("Game over. Player  Won");
                                        if((game).getPlayer().getPlayerToken() == Token.PLAYER_1) {
                                            p1Wins++;
                                            finishLine = game.getPlayer().getName() + " Win!!";
                                        } else {
                                            p2Wins++;
                                            finishLine = game.getPlayer().getName() + " Win!!";
                                        }
                                        finishDesc = "Hurray!!\n Game won.\n\n Would you like to play a new game";
                                        showDialog(finishLine, finishDesc);
                                    }
                                    numberMoves = 0;
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

            }
            return true;
        }
    };

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

    public void buttonGame(View view) throws IOException, GameException {
        int id  = view.getId();
        if(id == R.id.servergame){
            initServerGame();
        }
        if(id == R.id.clientgame){
            initClientGame();
        }
    }

    private void initServerGame() throws GameException, IOException {
        gs = new GameServer();
        gc = new GameClient(Token.PLAYER_1);

        // display IP addresses
        Enumeration<NetworkInterface> nets;
        Enumeration<InetAddress> inetAddresses;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    com.esotericsoftware.minlog.Log.info(inetAddress.toString());
                    System.out.println(inetAddress.getHostAddress());
                }
            }
        } catch (SocketException e) { e.printStackTrace(); }
        p = new HumanPlayer("Miguel",Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER);
        game.setPlayer(p);

        firstTry = true;

        new AsyncCaller().execute();

    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(NetMainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            try {
                gc.connectToServer("localhost");
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(true) {
                if(gs.hasConnectionEstablished()) {
                    break;
                }
                if(firstTry) {
                    Log.info("Server initialized. Waiting for connection from GameClient of this computer");
                    firstTry = false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //this method will be running on UI thread
            pdLoading.dismiss();
            setContentView(R.layout.activity_net_main);
        }
    }
    private void initClientGame() throws GameException {

        gc = new GameClient(Token.PLAYER_2);
        p = new HumanPlayer("Aida",Token.PLAYER_2, Game.NUM_PIECES_PER_PLAYER);
        game.setPlayer(p);

        new AsyncConnect().execute();

    }
    private class AsyncConnect extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(NetMainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tconnecting...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            while(true) {
                try {
                    System.out.println("Connect to GameServer at IP address: 172.19.13.33");
                    gc.connectToServer("172.19.13.33");
                    break;
                } catch (Exception e) {
                    Log.info("No GameServer detected!");
                    if(--numberTries == 0) {
                        System.exit(-1);
                    }
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    Log.info("Trying another connection.");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //this method will be running on UI thread
            pdLoading.dismiss();
            setContentView(R.layout.activity_net_main);
        }
    }
}
