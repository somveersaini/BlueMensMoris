package org.bluechat.blueninemenmoris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import org.bluechat.blueninemenmoris.model.AIPlayer;
import org.bluechat.blueninemenmoris.model.Board;
import org.bluechat.blueninemenmoris.model.Game;
import org.bluechat.blueninemenmoris.model.GameException;
import org.bluechat.blueninemenmoris.model.HumanPlayer;
import org.bluechat.blueninemenmoris.model.LocalGame;
import org.bluechat.blueninemenmoris.model.MinimaxAIPlayer;
import org.bluechat.blueninemenmoris.model.Move;
import org.bluechat.blueninemenmoris.model.Player;
import org.bluechat.blueninemenmoris.model.RandomAIPlayer;
import org.bluechat.blueninemenmoris.model.Token;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Board board;
    private GameView gameView;
    public LocalGame game;
    public static final int MAX_MOVES = 150;
    public static int totalMoves = 0;
    private int numberGames = 0, fixedNumberGames = 0, numberMoves = 0, draws = 0, p1Wins = 0, p2Wins = 0;
    private  long gamesStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        gameView = (GameView)findViewById(R.id.gameView);

        game = gameView.getGame();
        board = gameView.getBoard();
      //  initGame(5);



    }

    private void initGame(int minimaxDepth) {
        System.out.println("Player 1: (H)UMAN or (C)PU?");
        String userInput = "H";
        userInput = userInput.toUpperCase();
        Player p1 = null, p2 = null;
        boolean bothCPU = true;

        try {
        if(userInput.compareTo("HUMAN") == 0 || userInput.compareTo("H") == 0) {
            p1 = new HumanPlayer("Miguel", Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER);
            bothCPU = false;
        } else if(userInput.compareTo("CPU") == 0 || userInput.compareTo("C") == 0) {
            p1 = new RandomAIPlayer(Token.PLAYER_1,Game.NUM_PIECES_PER_PLAYER);
//			p1 = new MinimaxIAPlayer(Token.PLAYER_1, Game.NUM_PIECES_PER_PLAYER, minimaxDepth);
        } else {
            System.out.println("Command unknown");
            System.exit(-1);
        }

        System.out.println("Player 2: (H)UMAN or (C)PU?");
        userInput = "C";
        userInput = userInput.toUpperCase();

        if(userInput.compareTo("HUMAN") == 0 || userInput.compareTo("H") == 0) {
            p2 = new HumanPlayer("Miguel", Token.PLAYER_2, Game.NUM_PIECES_PER_PLAYER);
            bothCPU = false;
        } else if(userInput.compareTo("CPU") == 0 || userInput.compareTo("C") == 0) {
//			p2 = new RandomAIPlayer(Token.PLAYER_2,Game.NUM_PIECES_PER_PLAYER);
            p2 = new MinimaxAIPlayer(Token.PLAYER_2,Game.NUM_PIECES_PER_PLAYER, minimaxDepth-2);
        } else {
            System.out.println("Command unknown");
            System.exit(-1);
        }
        } catch (GameException e) {
            e.printStackTrace();
        }
        if(bothCPU) {
            System.out.println("Number of games: ");
            userInput = "5";
            numberGames = Integer.parseInt(userInput.toUpperCase());
            fixedNumberGames = numberGames;
        } else {
            numberGames = 1;
        }

        game = new LocalGame();
        ((LocalGame)game).setPlayers(p1, p2);
        gamesStart = System.nanoTime();
    }
/*
    public void gameInput(String userInput) throws GameException{
        if(game.getCurrentGamePhase() == Game.PLACING_PHASE) {
            Player p = ((LocalGame) game).getCurrentTurnPlayer();
            int boardIndex;

            if (p.isAI()) {
                long startTime = System.nanoTime();
//						System.out.println("AI THINKING");
                boardIndex = ((AIPlayer) p).getIndexToPlacePiece(game.getGameBoard());
                long endTime = System.nanoTime();
                //					game.printGameBoard();
                Log.d("Number of moves:", " " + ((AIPlayer) p).numberOfMoves);
                Log.d("Moves that removed:", " " + ((AIPlayer) p).movesThatRemove);
                Log.d("It took:", "" + (endTime - startTime) / 1000000 + " miliseconds");
//						System.out.println(p.getName()+" placed piece on "+boardIndex);

            } else {
                game.printGameBoard();
                System.out.println(p.getName() + " place piece on: ");
                userInput = input.readLine();
                userInput = userInput.toUpperCase();
                boardIndex = Integer.parseInt(userInput);
            }

            if (game.placePieceOfPlayer(boardIndex, p.getPlayerToken())) {
                numberMoves++; // TODO testing
                totalMoves++;
                p.raiseNumPiecesOnBoard();

                if (game.madeAMill(boardIndex, p.getPlayerToken())) {
                    Token opponentPlayer = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;

                    while (true) {
                        if (p.isAI()) {
                            boardIndex = ((AIPlayer) p).getIndexToRemovePieceOfOpponent(game.getGameBoard());
//									System.out.println(p.getName()+" removes opponent piece on "+boardIndex);
                        } else {
                            System.out.println("You made a mill. You can remove a piece of your oponent: ");
                            userInput = input.readLine();
                            userInput = userInput.toUpperCase();
                            boardIndex = Integer.parseInt(userInput);
                        }
                        if (game.removePiece(boardIndex, opponentPlayer)) {
                            break;
                        } else {
                            System.out.println("You can't remove a piece from there. Try again");
                        }
                    }
                }
                ((LocalGame) game).updateCurrentTurnPlayer();

            } else {
                System.out.println("You can't place a piece there. Try again");
            }
        }

    }
    public void createLocalGame() throws IOException, GameException {



        while(numberGames > 0) {
            if((numberGames-- % 50) == 0){
                System.out.println("Games left: "+numberGames);
            }

            while(game.getCurrentGamePhase() == Game.PLACING_PHASE) {


            }

//			System.out.println("The pieces are all placed. Starting the fun part... ");
            while(!game.isTheGameOver() && numberMoves < MAX_MOVES) {

                while(true) {
//					System.out.println("Number of moves made: "+numberMoves);
                    Player p = ((LocalGame)game).getCurrentTurnPlayer();
                    int srcIndex, destIndex;
                    Move move = null;

                    if(p.isAI()) {
//						long startTime = System.nanoTime();
                        //System.out.println("AI THINKING");
                        move = ((AIPlayer)p).getPieceMove(game.getGameBoard(), game.getCurrentGamePhase());
//						long endTime = System.nanoTime();
//						game.printGameBoard();

                        //System.out.println("Number of moves: "+((MinimaxIAPlayer)p).numberOfMoves);
                        //					System.out.println("Moves that removed: "+((MinimaxIAPlayer)p).movesThatRemove);
//						System.out.println("It took: "+ (endTime - startTime)/1000000+" miliseconds");
                        srcIndex = move.srcIndex;
                        destIndex = move.destIndex;
//						System.out.println(p.getName()+" moved piece from "+srcIndex+" to "+destIndex);
                    } else {
                        game.printGameBoard();
//						System.out.println(p.getName()+" it's your turn. Input PIECE_POS:PIECE_DEST");
                        userInput = input.readLine();
                        userInput = userInput.toUpperCase();
                        String[] positions = userInput.split(":");
                        srcIndex = Integer.parseInt(positions[0]);
                        destIndex = Integer.parseInt(positions[1]);
                        System.out.println("Move piece from "+srcIndex+" to "+destIndex);
                    }

                    int result;
                    if((result = game.movePieceFromTo(srcIndex, destIndex, p.getPlayerToken())) == Game.VALID_MOVE) {
                        numberMoves++; // TODO testing
                        totalMoves++;
                        if(game.madeAMill(destIndex, p.getPlayerToken())) {
                            Token opponentPlayerToken = (p.getPlayerToken() == Token.PLAYER_1) ? Token.PLAYER_2 : Token.PLAYER_1;
                            int boardIndex;

                            while(true) {
                                if(p.isAI()){
                                    boardIndex = move.removePieceOnIndex;
//									System.out.println(p.getName()+" removes opponent piece on "+boardIndex);
                                } else {
                                    //System.out.println("You made a mill! You can remove a piece of your oponent: ");
                                    userInput = input.readLine();
                                    userInput = userInput.toUpperCase();
                                    boardIndex = Integer.parseInt(userInput);
                                }
                                if(game.removePiece(boardIndex, opponentPlayerToken)) {
                                    break;
                                } else {
                                    //System.out.println("It couldn't be done! Try again.");
                                }
                            }
                        }


                        if(game.isTheGameOver() || numberMoves >= MAX_MOVES) {
//							game.printGameBoard();
                            break;
                        }
                        ((LocalGame)game).updateCurrentTurnPlayer();
                    } else {
                        System.out.println("Invalid move. Error code: "+result);
                    }
                }
            }

            if(!game.isTheGameOver()) {
//				System.out.println("Draw!");
                draws++;
            } else {
//				System.out.println("Game over. Player "+((LocalGame)game).getCurrentTurnPlayer().getPlayerToken()+" Won");
                if(((LocalGame)game).getCurrentTurnPlayer().getPlayerToken() == Token.PLAYER_1) {
                    p1Wins++;
                } else {
                    p2Wins++;
                }
            }
            numberMoves = 0;
            game = new LocalGame();
            p1.reset();
            p2.reset();
            ((LocalGame)game).setPlayers(p1, p2);
        }
        long gamesEnd = System.nanoTime();
        System.out.println(fixedNumberGames+" games completed in: "+ (gamesEnd - gamesStart)/1000000000+" seconds");
        System.out.println("Average number of ply: "+(totalMoves/fixedNumberGames));
        System.out.println("Draws: "+draws+" ("+((float)draws/fixedNumberGames)*100+"%)");
        System.out.println("P1 Wins: "+p1Wins+" ("+((float)p1Wins/fixedNumberGames)*100+"%)");
        System.out.println("P2 Wins: "+p2Wins+" ("+((float)p2Wins/fixedNumberGames)*100+"%)");
    }
    */
}
