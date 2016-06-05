package org.bluechat.blueninemenmoris.model;

public class HumanPlayer extends Player {
		
	public HumanPlayer(String name, Token player, int numPiecesPerPlayer) throws GameException {
		super(player, numPiecesPerPlayer);
		this.name = name;
	}

	@Override
	public boolean isAI() {
		return false;
	}
}
