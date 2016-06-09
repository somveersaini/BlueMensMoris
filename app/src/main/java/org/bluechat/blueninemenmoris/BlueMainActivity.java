package org.bluechat.blueninemenmoris;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bluechat.blueninemenmoris.Bluetooth.BluetoothChatService;
import org.bluechat.blueninemenmoris.Bluetooth.Constants;
import org.bluechat.blueninemenmoris.model.Actor;
import org.bluechat.blueninemenmoris.model.Board;
import org.bluechat.blueninemenmoris.model.Game;
import org.bluechat.blueninemenmoris.model.GameException;
import org.bluechat.blueninemenmoris.model.HumanPlayer;
import org.bluechat.blueninemenmoris.model.LocalGame;
import org.bluechat.blueninemenmoris.model.MinimaxAIPlayer;
import org.bluechat.blueninemenmoris.model.Player;
import org.bluechat.blueninemenmoris.model.Token;

public class BlueMainActivity extends AppCompatActivity {

    public static final int MAX_MOVES = 150;
    private static final String TAG = "MainActity";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public static int totalMoves = 0;
    public String name = "Player One";
    public LocalGame game;
    boolean myturn = true;
    int chal = 0;
    int COL = 8;
    SharedPreferences settings;
    Actor currActor = null;
    Typeface typeface, typeface1;
    Player p1, p2;
    Handler handler;
    long HANDLER_DELAY = 10;
    TextView top;
    TextView bottom;
    String myname = null;
    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private Actor currentBlueActor;
    private int backButtonCount = 0;
    private long backButtonPreviousTime = 0;
    private boolean backButtonMessageHasBeenShown = false;
    private boolean starter = true;
    private Board board;
    private GameView gameView;
    private int numberGames = 1, fixedNumberGames = 1, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;
    private long gamesStart;
    private int removedPieceP1, removedPieceP2;
    private int starttime = 0, currenttime = 0;
    private VelocityTracker mVelocityTracker = null;
    private boolean madeamill = false;
    private int offsetX;
    private int offsetY;
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getParent();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            // setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            Log.d(TAG, "handleMessage:  connected");
                            String message = Constants.PLAYER_2 + " " + myname;
                            sendmessage(message);

                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            // setStatus("Connecting...");
                            Log.d(TAG, "handleMessage: connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            break;
                        case BluetoothChatService.STATE_NONE:
                            Log.d(TAG, "handleMessage: state none");
                            Log.d(TAG, "deviceListResult secure: not connected");
                            Intent serverIntent = new Intent(BlueMainActivity.this, DeviceListActivity.class);
                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                            ensureDiscoverable();
                            //  setStatus("Currently not connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("sam", readMessage);
                    String[] m = readMessage.split(" ");
                    //bluetoothinput(Integer.parseInt(m[0]), m[1]);
                    if (m.length == 3) {
                        bluetoothinput(Integer.parseInt(m[0]), Integer.parseInt(m[1]), Integer.parseInt(m[2]));
                    } else {
                        bluetoothinput(Integer.parseInt(m[0]), m[1]);
                    }
                    // TODO: 06/09/2016 message recieved handle bluetoth input
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }


    };
    View.OnTouchListener gameListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);

            String msg = null;
            if (game.getCurrentTurnPlayer().getName().equals("you")) {
                if (action == MotionEvent.ACTION_DOWN) {
                    int y = (int) event.getY();
                    int x = (int) event.getX();
                    msg = Constants.DOWN + " " + x + " " + y;
                    sendMessage(msg);

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
                        //  Log.d("selected opponent piece", " " + mini);
                    }
                    starttime = currenttime;

                } else if (action == MotionEvent.ACTION_MOVE) {
                    int y = (int) event.getY();
                    int x = (int) event.getX();
                    msg = Constants.DOWN + " " + x + " " + y;
                    sendMessage(msg);

                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    int xvel = (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                    int yvel = (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

                    int vel = (int) Math.sqrt(xvel * xvel + yvel * yvel);


                    // Log.d("moving", x + " " + y);
                    if (currActor != null) {
                        currActor.setPosxy(x - offsetX, y - offsetY);
                    }

                } else if (action == MotionEvent.ACTION_UP) {

                    int y = (int) event.getY();
                    int x = (int) event.getX();
                    msg = Constants.DOWN + " " + x + " " + y;
                    sendMessage(msg);

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

                                            currActor.setRemoved(true);
                                            madeamill = false;
                                            game.updateCurrentTurnPlayer();

                                            //TODO : send and update to bluetooth device

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
                                            } else {
                                                System.out.println("changed current Player");
                                                game.updateCurrentTurnPlayer();

                                                //TODO : send and update to bluetooth device
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

                                                //TODO : send and update to bluetooth device

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
                                                } else {
                                                    game.updateCurrentTurnPlayer();
                                                    System.out.println("changed current Player");

                                                    //TODO : send and update to bluetooth device

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
                        }
                    } else {
                        currActor = null;
                    }

                }
            }
            return true;
        }

    };

    private void bluetoothinput(int msg, String name) {
        switch (msg) {
            case Constants.PLAYER_1:
                p1.setName(name);
                p2.setName("you");
                break;

            case Constants.PLAYER_2:
                p1.setName("you");
                p2.setName(name);
                break;
        }
    }

    private void sendmessage(String msg) {
        sendMessage(msg);
    }

    private void bluetoothinput(int msg, int x, int y) {
        switch (msg) {
            case Constants.DOWN:
                //handle ACTION_DOWN from other device

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
                                currentBlueActor = actor;
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
                                    currentBlueActor = actor;
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
                                    currentBlueActor = actor;
                                }
                            }
                        }
                    }
                }
                Log.d("currentmin", " " + min);
                if (min > 80) {
                    currentBlueActor = null;
                } else {
                    //  Log.d("selected opponent piece", " " + mini);
                }


                break;
            case Constants.MOVE:
                //handle ACTION_MOVE from other device

                if (currentBlueActor != null) {
                    currentBlueActor.setPosxy(x - offsetX, y - offsetY);
                }

                break;
            case Constants.UP:
                //handle ACTION_UP from other device
                Log.d("action", "up");
                min = 1000;
                int mini = -1;
                if (currentBlueActor != null) {
                    if (madeamill) {
                        mini = currentBlueActor.getPlacedIndex();
                        min = 1;
                    } else {
                        for (int i = 0; i < Board.NUM_POSITIONS_OF_BOARD; i++) {
                            try {
                                if (board.positionIsAvailable(i)) {
                                    int t1 = board.getY(i) - (currentBlueActor.getPosy());
                                    int t2 = board.getX(i) - (currentBlueActor.getPosx());
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
                                            currentBlueActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                                            //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                        } else {
                                            ++removedPieceP2;
                                            currentBlueActor.setPosxy(gameView.getP2rx(), gameView.getP2ry(removedPieceP2));
                                            //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                        }

                                        currentBlueActor.setRemoved(true);
                                        madeamill = false;
                                        game.updateCurrentTurnPlayer();

                                        //TODO : send and update to bluetooth device

                                    } else {
                                        System.out.println("You can't remove a piece from there. Try again");
                                    }
                                } else {
                                    if (game.placePieceOfPlayer(boardIndex, p.getPlayerToken())) {
                                        Log.d("selected", "pos " + mini);
                                        numberMoves++; // TODO testing
                                        totalMoves++;
                                        p.raiseNumPiecesOnBoard();
                                        currentBlueActor.setPosxy(board.getX(mini), board.getY(mini));
                                        currentBlueActor.setPlacedIndex(mini);

                                        if (game.madeAMill(boardIndex, p.getPlayerToken())) {
                                            madeamill = true;
                                            System.out.println("You made a mill. You can remove a piece of your oponent: ");
                                        } else {
                                            System.out.println("changed current Player");
                                            game.updateCurrentTurnPlayer();

                                            //TODO : send and update to bluetooth device
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
                                        boardIndex = currentBlueActor.getPlacedIndex();
                                        // Log.d("removing at pos", ""+ mini);
                                        Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                                        if (game.removePiece(boardIndex, opponentPlayer)) {
                                            System.out.println("removed piece at " + boardIndex);
                                            if (opponentPlayer == Token.PLAYER_1) {
                                                ++removedPieceP1;
                                                currentBlueActor.setPosxy(gameView.getP1rx(), gameView.getP1ry(removedPieceP1));
                                                //  Log.d("removed of 1 ", "placed at "+ " " + squareStart/2 + " " +(squareSpace) + ((removedPieceP1 + 1) * removedSpace));

                                            } else {
                                                ++removedPieceP2;
                                                currentBlueActor.setPosxy(gameView.getP2rx(), gameView.getP2ry(removedPieceP2));
                                                //  Log.d("removed of 2 ", "placed at "+ (viewWidth - (squareStart/2))+ " " + ((viewWidth + squareSpace) - (removedPieceP2 * removedSpace)));
                                            }

                                            currentBlueActor.setRemoved(true);
                                            madeamill = false;

                                            game.updateCurrentTurnPlayer();

                                            //TODO : send and update to bluetooth device

                                        } else {
                                            System.out.println("You can't remove a piece from there. Try again");
                                        }
                                    } else {
                                        int srcIndex, destIndex;
                                        srcIndex = currentBlueActor.getPlacedIndex();
                                        destIndex = mini;
                                        System.out.println("Move piece from " + srcIndex + " to " + destIndex);

                                        int result;
                                        if ((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken())) == Game.VALID_MOVE) {
                                            numberMoves++; // TODO testing
                                            totalMoves++;
                                            currentBlueActor.setPosxy(board.getX(mini), board.getY(mini));
                                            currentBlueActor.setPlacedIndex(mini);
                                            if (game.madeAMill(destIndex, p.getPlayerToken())) {
                                                madeamill = true;
                                            } else {
                                                game.updateCurrentTurnPlayer();
                                                System.out.println("changed current Player");

                                                //TODO : send and update to bluetooth device

                                            }
                                        } else {
                                            currentBlueActor.setPosxy(board.getX(srcIndex), board.getY(srcIndex));
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
                        if (currentBlueActor.getPlacedIndex() == -1) {
                            currentBlueActor.setToPreviousPosition();
                        } else {
                            currentBlueActor.setPosxy(board.getX(currentBlueActor.getPlacedIndex()), board.getY(currentBlueActor.getPlacedIndex()));
                        }
                    }
                } else {
                    currentBlueActor = null;
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            // Activity activity = this;
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        typeface = Typeface.createFromAsset(getAssets(),
                "Gasalt-Black.ttf");
        typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");

        handler = new Handler();
        gameView = (GameView) findViewById(R.id.gameView);

        top = (TextView) findViewById(R.id.top);
        bottom = (TextView) findViewById(R.id.bottom);

        try {
            game = new LocalGame();
            p1 = new HumanPlayer("sam", Token.PLAYER_1, 9);
            if (getIntent().getBooleanExtra("isAI", false)) {
                p2 = new MinimaxAIPlayer(Token.PLAYER_2, 9, 4);
            } else {
                p2 = new HumanPlayer("kuku", Token.PLAYER_2, 9);
            }
            game.setPlayers(p1, p2);
            board = game.getGameBoard();
            gameView.setGame(game);
        } catch (GameException e) {
            e.printStackTrace();
        }
        top.setText(p1.getName() + " ");
        bottom.setText(p2.getName() + " ");
        gameView.setOnTouchListener(gameListner);
        myname = "sam";
    }

    public void showDialog(String finishline, String finishdesc) {
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

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    //* Sends a message.(String message)
    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {

            //  Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            mOutStringBuffer.setLength(0);

        }
    }

    // * Updates the status on the action bar. usin resID
    private void setStatus(int resId) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
    }

    private void setStatus(CharSequence subTitle) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
        //TextView g = (TextView) findViewById(R.id.bluename);
        //g.setText(subTitle);
        sendMessage(-1 + " " + name);
        // refresh();
        //adapter.turn = 0;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);

                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }

    //* Establish connection with other divice
    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
        if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            String msg = Constants.PLAYER_1 + " " + game.getCurrentTurnPlayer().getName();
            sendMessage(msg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        if (mChatService == null) {
            setupChat();
        }
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Log.d(TAG, "onStart: not connected");
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            ensureDiscoverable();
        }
//        Intent serverIntent = new Intent(this, DeviceListActivity.class);
//        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//        ensureDiscoverable();
    }

    public void bluetoothSetup() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        // save();
        // mp.stop();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }

        }
    }
}
