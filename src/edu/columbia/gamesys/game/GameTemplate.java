package edu.columbia.gamesys.game;

public abstract class GameTemplate {
	private Player player1;
	private Player player2;
	private int gameNumber;
	private boolean gameOver;
	private boolean draw;
	private Player winner;
	
	
	public Player getPlayer1() {
		return player1;
	}
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}
	public Player getPlayer2() {
		return player2;
	}
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}
	public int getGameNumber() {
		return gameNumber;
	}
	public void setGameNumber(int gameNumber) {
		this.gameNumber = gameNumber;
	}
	public boolean isGameOver() {
		return gameOver;
	}
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	public boolean isDraw() {
		return draw;
	}
	public void setDraw(boolean draw) {
		this.draw = draw;
	}
	public Player getWinner() {
		return winner;
	}
	public void setWinner(Player winner) {
		this.winner = winner;
	}

}
