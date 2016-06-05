package org.bluechat.blueninemenmoris.model;


public abstract class Player {
	
	protected String name;
	protected int gamesWon;
	protected int numPieces;
	protected int numPiecesOnBoard;
	protected Token playerToken;
	protected boolean canFly;
	protected Actor[] actors;
	
	protected Player() {
		gamesWon = 0;
		numPiecesOnBoard = 0;
		canFly = false;
	}
	
	protected Player(Token player, int numPiecesPerPlayer) throws GameException {
		this();
		if(player != Token.PLAYER_1 && player != Token.PLAYER_2) {
			throw new GameException(""+getClass().getName()+" - Invalid Player Token: "+player);
		} else {
			numPieces = numPiecesPerPlayer;
			playerToken = player;
            actors = new Actor[numPieces];
		}
	}
	
	public void reset() {
		numPiecesOnBoard = 0;
		canFly = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumPieces() {
		return numPieces;
	}
	
	public int getNumPiecesOnBoard() {
		return numPiecesOnBoard;
	}
	
	public int getNumPiecesLeftToPlace() {
		return (numPieces - numPiecesOnBoard);
	}
	
	public int raiseNumPiecesOnBoard() {
		canFly = false; // it's still placing pieces
		return ++numPiecesOnBoard;
	}
	
	public int lowerNumPiecesOnBoard() {
		if(--numPiecesOnBoard == 3) {
			canFly = true;
		}
		return numPiecesOnBoard;
	}
	
	public Token getPlayerToken() {
		return playerToken;
	}
	
	public boolean canItFly() {
		return canFly;
	}
	
	public abstract boolean isAI();

    public Actor[] getActors() {
        return actors;
    }
    public void setActors(int sx, int sy){
        for (int i = 0; i < numPieces; i++) {
            actors[i] = new Actor((i + 1)*sx, sy);
        }
    }
}
