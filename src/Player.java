
public class Player {
	String name;
	int playerport;
	int playerNumber;
	int opponentport;
	
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
    int getPlayerNumber(){
    	
    	return this.playerNumber;
    }
    
    String getName(){
    	return this.name;
    }
    
    int getPort() {
    	return this.playerport;
    }
    
    
}
