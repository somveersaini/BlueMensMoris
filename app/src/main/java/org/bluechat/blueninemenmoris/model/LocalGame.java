 package org.bluechat.blueninemenmoris.model;

 public class LocalGame extends Game {
	private Player player1;
	private Player player2;
	private Player currentTurnPlayer;
	
	public LocalGame() {
		super();
	}
	
	public void setPlayers(Player p1, Player p2) {
		player1 = p1;
		player2 = p2;
		currentTurnPlayer = player1;
	}

	public Player getPlayer() {
		return currentTurnPlayer;
	}
	 public Player getPlayer1() {
		 return player1;
	 }
	 public Player getPlayer2() {
		 return player2;
	 }

	public void updateCurrentTurnPlayer() {
		if(currentTurnPlayer.equals(player1)) {
			currentTurnPlayer = player2;
		} else {
			currentTurnPlayer = player1;
		}
	}
	
	public boolean removePiece(int boardIndex, Token player) throws GameException {
		if(super.removePiece(boardIndex, player)) {
			Player p = currentTurnPlayer.equals(player1) ? player2 : player1;
			p.lowerNumPiecesOnBoard();
			return true;
		}
		return false;
	}

	 public Player getCurrentTurnPlayer() {
		return currentTurnPlayer;
	}

	 public void setCurrentTurnPlayer(Player p1) {
		 currentTurnPlayer = p1;
	 }

     public Player getOpponentPlayer() {
         return currentTurnPlayer.equals(player1)?player2:player1;
     }
}
