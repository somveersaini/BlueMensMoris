package org.bluechat.blueninemenmoris;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
    boolean myturn = false;

    Actor currActor = null;
    Typeface typeface, typeface1;
    Player p1, p2;
    Handler handler;
    long HANDLER_DELAY = 10;
    TextView top, topdesc;
    TextView bottom, bottomdesc;
    String myname = null;
    float scalex = 1;
    float scaley = 1;
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
    private boolean madeamill = false;
    private int offsetX;
    private int offsetY;
    private Token mytoken = Token.NO_PLAYER;
    private long previousTime;

    View.OnTouchListener gameListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            String msg;
            if (game.getCurrentTurnPlayer().getPlayerToken() == mytoken) {
                if (action == MotionEvent.ACTION_DOWN) {
                    int y = (int) event.getY();
                    int x = (int) event.getX();
//                    msg = Constants.DOWN + " " + x + " " + y + " ";
//                    new sendBlue().execute();;

                    currenttime = (int) Math.abs(System.currentTimeMillis());
                    currenttime = Math.abs(currenttime);
                    // System.out.println(currenttime + " " + starttime);

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
                    if (min > 100) {
                        currActor = null;

                    } else {

                       // msg = Constants.SELECT + "-" + currActor.getNumber() + "-0-";
                        new sendBlue().execute(Constants.SELECT + "-" + currActor.getNumber() + "-0");
                        Log.d("selected opponent piece", " send");
                       // msg = Constants.SELECT + "-" + currActor.getNumber() + "-0-";
                       // new sendBlue().execute();
                       // msg = Constants.SELECT + "-" + currActor.getNumber() + "-0-";
                      //  new sendBlue().execute();
                    }

                } else if (action == MotionEvent.ACTION_MOVE) {
                    int y = (int) event.getY();
                    int x = (int) event.getX();



                    // Log.d("moving", x + " " + y);
                    if (currActor != null) {
                        currActor.setPosxy(x - offsetX, y - offsetY);
                       // final long currentTime = System.currentTimeMillis();
//                        if((currentTime - previousTime) >= 333) {
//                           // new sendBlue().execute(Constants.MOVE + "-" + x + "-" + y + "-");
//                            previousTime = currentTime;
//                        }
                       // msg = ;
                       // new sendBlue().execute(Constants.MOVE + "-" + x + "-" + y + "-");;
                    }

                } else if (action == MotionEvent.ACTION_UP) {

                    int y = (int) event.getY();
                    int x = (int) event.getX();
//                    msg = Constants.UP + " " + x + " " + y;
//                    new sendBlue().execute();;

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
                        if (min < 100 && mini != -1) {
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

                                          //  msg = Constants.SELECT + "-" + currActor.getNumber() + "-0-";
                                          //  new sendBlue().execute();;
                                            msg = Constants.REMOVE + "-" + currActor.getNumber() + "-" + boardIndex + "-";
                                            new sendBlue().execute(Constants.REMOVE + "-" + currActor.getNumber() + "-" + boardIndex + "-");;

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

                                           // msg = Constants.SELECT + "-" + currActor.getNumber() + "-0-";
                                           // new sendBlue().execute();;
                                            msg = Constants.PLACE + "-" + currActor.getNumber() + "-" + boardIndex + "-";
                                            new sendBlue().execute(Constants.PLACE + "-" + currActor.getNumber() + "-" + boardIndex + "-");;

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

                                              //  msg = Constants.SELECT + "-" + currActor.getNumber() + "-0-";
                                              //  new sendBlue().execute();;
                                              //  msg = Constants.REMOVE + "-" + currActor.getNumber() + "-" + boardIndex + "-";
                                                new sendBlue().execute(Constants.REMOVE + "-" + currActor.getNumber() + "-" + boardIndex + "-");;

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
                                              //  msg = Constants.SELECT + "-" + currActor.getNumber() + "-" + srcIndex + "-";
                                              //  new sendBlue().execute();;
                                              //  msg = Constants.PLACE + "-" + currActor.getNumber() + "-" + destIndex + "-";
                                                new sendBlue().execute(Constants.PLACE + "-" + currActor.getNumber() + "-" + destIndex + "-");;
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
    private int previousInpB = 1324;
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
                            String message = 3344 + "-" + Settings.pName + "-" + gameView.getViewWidth() + "-" + gameView.getViewHeight();
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
                    Log.d(TAG, "handleMessage: " + readMessage);
                    String[] m = readMessage.split("-");
                    //bluetoothinput(Integer.parseInt(m[0]), m[1]);
                    if (previousInpB != Constants.UP) {
                        previousInpB = Integer.parseInt(m[0]);
                        if (m.length == 3) {
                            Log.d("recieved :3", Integer.parseInt(m[0]) + " " + Integer.parseInt(m[1]) + " " + Integer.parseInt(m[2]));
                            bluetoothinput(Integer.parseInt(m[0]), Integer.parseInt(m[1]), Integer.parseInt(m[2]));
                        } else if (m.length == 4) {
                            bluetoothinput(Integer.parseInt(m[0]), m[1], Integer.parseInt(m[2]), Integer.parseInt(m[3]));
                            Log.d("recieved :4", Integer.parseInt(m[0]) + " " + m[1] + " " + Integer.parseInt(m[2]) + " " + Integer.parseInt(m[3]));
                        } else {
                            int inps = m.length / 3;
                            for (int i = 0; i < inps; i++) {
                                bluetoothinput(Integer.parseInt(m[i * 3]), Integer.parseInt(m[i * 3 + 1]), Integer.parseInt(m[i * 3 + 2]));
                                Log.d("recieved :5", Integer.parseInt(m[i * 3]) + " " + Integer.parseInt(m[i * 3 + 1]) + " " + Integer.parseInt(m[i * 3 + 2]));
                            }
                        }
                    } else {
                        if (Integer.parseInt(m[0]) != Constants.UP) {
                            previousInpB = Integer.parseInt(m[0]);
                            if (m.length == 3) {
                                Log.d("recieved :3", Integer.parseInt(m[0]) + " " + Integer.parseInt(m[1]) + " " + Integer.parseInt(m[2]));
                                bluetoothinput(Integer.parseInt(m[0]), Integer.parseInt(m[1]), Integer.parseInt(m[2]));
                            } else if (m.length == 4) {
                                bluetoothinput(Integer.parseInt(m[0]), m[1], Integer.parseInt(m[2]), Integer.parseInt(m[3]));
                                Log.d("recieved :4", Integer.parseInt(m[0]) + " " + m[1] + " " + Integer.parseInt(m[2]) + " " + Integer.parseInt(m[3]));
                            } else {
                                int inps = m.length / 3;
                                for (int i = 0; i < inps; i++) {
                                    bluetoothinput(Integer.parseInt(m[i * 3]), Integer.parseInt(m[i * 3 + 1]), Integer.parseInt(m[i * 3 + 2]));
                                    Log.d("recieved :5", Integer.parseInt(m[i * 3]) + " " + Integer.parseInt(m[i * 3 + 1]) + " " + Integer.parseInt(m[i * 3 + 2]));
                                }
                            }
                        }
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

    private void sendmessage(String msg) {
        new sendBlue().execute(msg);;
    }

    private void bluetoothinput(int msg, String name, int width, int height) {
        scalex = ((float) gameView.getViewWidth() / (float) width);
        scaley = ((float) gameView.getViewHeight() / (float) height);

        if (msg == 3344) {
            if (myturn) {
                mytoken = p1.getPlayerToken();
                p1.setName(Settings.pName);
                p2.setName(name);
                top.setText(p1.getName());
                bottom.setText(name);
            } else {
                mytoken = p2.getPlayerToken();
                p1.setName(name);
                p2.setName(Settings.pName);
                top.setText(name);
                bottom.setText(p2.getName());
            }
            topdesc.setText("Start buddy!!");
        }

    }

    private boolean checkoffset = false;
    private void bluetoothinput(int msg, int x, int y) {
        //Log.d(TAG, "bluetoothinput: x = " + x + " y = " + y);
        if (msg == Constants.DOWN || msg == Constants.UP || msg == Constants.MOVE) {
            x = (int) (x * scalex);
            y = (int) (y * scaley);
        }
        //Log.d(TAG, "scaled to : x = " + x + " y = " + y);
        switch (msg) {
            case Constants.SELECT:
                if (madeamill) {
                    currentBlueActor = game.getOpponentPlayer().getActorByNumber(x);
                } else {
                    currentBlueActor = game.getCurrentTurnPlayer().getActorByNumber(x);
                }
                checkoffset = true;

                break;

            case Constants.PLACE:
                if(currentBlueActor != null) {
                    if (game.getCurrentGamePhase() == Game.PLACING_PHASE) {
                        // Log.d("placing phase", "onTouchEvent: removing");

                        try {
                            game.placePieceOfPlayer(y, game.getCurrentTurnPlayer().getPlayerToken());
                            numberMoves++; // TODO testing
                            totalMoves++;
                            game.getCurrentTurnPlayer().raiseNumPiecesOnBoard();
                            currentBlueActor.setPosxy(board.getX(y), board.getY(y));
                            currentBlueActor.setPlacedIndex(y);

                            if (game.madeAMill(y, game.getCurrentTurnPlayer().getPlayerToken())) {
                                madeamill = true;
                                currentBlueActor = null;
                                System.out.println("You made a mill. You can remove a piece of your oponent: ");
                            } else {
                                System.out.println("changed current Player");
                                game.updateCurrentTurnPlayer();

                                //TODO : send and update to bluetooth device
                            }
                        } catch (GameException e) {
                            e.printStackTrace();
                        }
                    } else {
                        int srcIndex, destIndex;
                        srcIndex = currentBlueActor.getPlacedIndex();
                        destIndex = y;
                        System.out.println("Move piece from " + srcIndex + " to " + destIndex);

                        int result;
                        try {
                            if ((result = game.movePieceFromTo(srcIndex, destIndex, game.getCurrentTurnPlayer().getPlayerToken())) == Game.VALID_MOVE) {
                                numberMoves++; // TODO testing
                                totalMoves++;
                                currentBlueActor.setPosxy(board.getX(destIndex), board.getY(destIndex));
                                currentBlueActor.setPlacedIndex(destIndex);
                                if (game.madeAMill(destIndex, game.getCurrentTurnPlayer().getPlayerToken())) {
                                    madeamill = true;
                                    currentBlueActor = null;
                                } else {
                                    game.updateCurrentTurnPlayer();
                                    System.out.println("changed current Player");

                                    //TODO : send and update to bluetooth device

                                }
                            } else {
                                currentBlueActor.setPosxy(board.getX(srcIndex), board.getY(srcIndex));
                                System.out.println("Invalid move. Error code: " + result);

                            }
                        } catch (Exception e) {

                        }
                        if (game.isTheGameOver()) {
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
                            totalMoves = 0;
                            //reset the game
                        }
                    }
                }
                break;
            case Constants.REMOVE:
                if(currentBlueActor != null) {
                    try {
                        if (madeamill) {
                            // Log.d("removing at pos", ""+ mini);
                            Token opponentPlayer = (game.getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                            if (game.removePiece(y, opponentPlayer)) {
                                System.out.println("removed piece at " + y);
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
                        }
                    } catch (GameException e) {
                        e.printStackTrace();
                    }
                    if (game.isTheGameOver()) {
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
                        totalMoves = 0;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            // Activity activity = this;
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }
        previousTime = System.currentTimeMillis();

        typeface = Typeface.createFromAsset(getAssets(),
                "CarterOne.ttf");
        typeface1 = Typeface.createFromAsset(getAssets(),
                "future.otf");

        handler = new Handler();
        gameView = (GameView)findViewById(R.id.gameView);

        top = (TextView) findViewById(R.id.top);
        bottom = (TextView) findViewById(R.id.bottom);
        topdesc = (TextView) findViewById(R.id.topdesc);
        bottomdesc = (TextView) findViewById(R.id.bottomdesc);
        top.setTypeface(typeface);
        topdesc.setTypeface(typeface);
        bottom.setTypeface(typeface);
        bottomdesc.setTypeface(typeface);


        Settings.load(getApplicationContext());
        try {
            game = new LocalGame();
            p1 = new HumanPlayer("sam", Token.PLAYER_1, 9);
            p2 = new HumanPlayer("Heyy!!", Token.PLAYER_2, 9);

            game.setPlayers(p1, p2);
            board = game.getGameBoard();
            gameView.setGame(game);
        } catch (GameException e) {
            e.printStackTrace();
        }
        refresh();
    }

    public void refresh(){
        numberMoves = 0;
        p1.setName(Settings.pName);
        top.setText(p1.getName());
        bottom.setText("How you doin?");
        topdesc.setText("Welcome to Blue Men's Morris");
        bottomdesc.setText("Click on right to connect");
        gameView.setOnTouchListener(gameListner);
        gameView.invalidate();
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
    private synchronized void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {

            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            mOutStringBuffer.setLength(0);

        }
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
        myturn = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
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

    public void startblue(View view) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
        ensureDiscoverable();
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
              //  init();
                alertDialog.cancel();
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlueMainActivity.this, Settings.class));
                alertDialog.cancel();
            }
        });
        Button helps = (Button) view.findViewById(R.id.helps);
        helps.setTypeface(typeface);

        helps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlueMainActivity.this, AboutActivity.class));
                alertDialog.cancel();
            }
        });
        //  alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();
    }
    private class sendBlue extends AsyncTask<String, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            sendMessage(params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
