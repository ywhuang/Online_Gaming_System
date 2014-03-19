package edu.columbia.gamesys.game;
public class Player {
	private String name;
	private int playerport;
	private int playerNumber;
	private int opponentport;
	
    public Player(String name, int port, int playerNumber){
    	this.name = name;
    	this.playerport = port;
    	this.playerNumber = playerNumber;
    	
    }
    /*
    public Player(String name) {
         this.name = name;
         this.playerport = 0;
         this.playerNumber = 1;
    }
    */
    public int getPlayerNumber(){
    	
    	return this.playerNumber;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public int getPort() {
    	return this.playerport;
    }
	public int getPlayerport() {
		return playerport;
	}
	public void setPlayerport(int playerport) {
		this.playerport = playerport;
	}
	public int getOpponentport() {
		return opponentport;
	}
	public void setOpponentport(int opponentport) {
		this.opponentport = opponentport;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	} 
    
}
