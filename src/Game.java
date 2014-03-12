import java.util.ArrayList;


public class Game {
	Player player1;
	Player player2;
	int gameNumber;
	boolean gameOver = false;
	boolean draw = false;
	Player winner;
	//Player loser;
	
	
	String board = "888888888";
	int boardDigits =888888888;
	//String board = "\n"+"1 "+"_ "+"_ "+"\n"+"_ "+"_ "+"_ "+"\n"+"_ "+"_ "+"_ " +"\n";
	//String[] boardArray = {"\n","_","_","_", "_","_","_", "_","_","_"};	
	 String[] boardStringArray = {"0","0","0","0","0","0","0","0","0"};
	 int[] boardIntArray = {0,0,0,0,0,0,0,0,0,0};
	//static ArrayList<Integer> winningLine1 = new ArrayList<Integer>();
	
	//static ArrayList<ArrayList> winningset = new <ArrayList>();
	
	ArrayList<Integer> player1Record = new ArrayList<Integer>();
    ArrayList<Integer> player2Record = new ArrayList<Integer>();
    ArrayList<Integer> judgeView = new ArrayList<Integer>(); 
    Integer[] Board = new Integer[9];
	int turn = 1;
	
    public Game(Player player1, Player player2, int gameNumber){
    	
    	//System.out.println("Establishing game..");
    	this.player1= player1;
    	this.player2= player2;
    	this.gameNumber = gameNumber;
    	
    	
    }
    
    void player1Move(int position){
    	
    	System.out.println("1 move :" + position);
    	judgeView.add(position);
    	player1Record.add(position);
    	//boardStringArray[position] = "1";
    	boardIntArray[position] = 1;
    }
    void player2Move(int position){
    	System.out.println("2 move :" + position);
    	judgeView.add(position);
    	player2Record.add(position);
    	//boardStringArray[position] = "2";
    	boardIntArray[position] = 2;
    }
    
    boolean isGameOver(){
    	
    	return this.gameOver;
    }
    
    boolean checkWin(ArrayList<Integer> playerRecord){
    	
    	if (playerRecord.size() >2) {
          if ( (playerRecord.contains(1) && playerRecord.contains(2) && playerRecord.contains(3)) ||
        	   (playerRecord.contains(4) && playerRecord.contains(5) && playerRecord.contains(6)) ||
        	   (playerRecord.contains(7) && playerRecord.contains(8) && playerRecord.contains(9)) ||
        	   (playerRecord.contains(1) && playerRecord.contains(4) && playerRecord.contains(7)) ||
        	   (playerRecord.contains(2) && playerRecord.contains(5) && playerRecord.contains(8)) ||
        	   (playerRecord.contains(3) && playerRecord.contains(6) && playerRecord.contains(9)) ||
        	   (playerRecord.contains(1) && playerRecord.contains(5) && playerRecord.contains(9)) ||
        	   (playerRecord.contains(3) && playerRecord.contains(5) && playerRecord.contains(7)) 
        		
        		) {
        	  this.gameOver = true;
        	  return true ;
          }
        }
    	else if (judgeView.size()>8){
              
    		this.draw = true;
    		this.gameOver = true;
    		
    	}
    	return false;
    }
    
    boolean isFull() {
    	
    	if ( judgeView.size()>8) {
    		
    		return true;
    	}
    	
    	return false;
    }
    
    String displayEmptyBoard(){
    	String emptyBoard = "\n"+"_ "+"_ "+"_ "+"\n"+"_ "+"_ "+"_ "+"\n"+"_ "+"_ "+"_ " +"\n";
    	
    	return emptyBoard;
    }
    
    String displayEmptyBoardDigits() {
    	String emptyBoard = "000000000";
    	return emptyBoard;
    }
    
    
    String displayBoard(){
    	board = "\n"+
                boardStringArray[1]+" "+boardStringArray[2]+" "+boardStringArray[3]+"\n"+
                boardStringArray[4]+" "+boardStringArray[5]+" "+boardStringArray[6]+"\n"+
                boardStringArray[7]+" "+boardStringArray[8]+" "+boardStringArray[9]+"\n";
    	
    	return board;
    }
    int displayBoardDigits(){
    	
    	/*
    	boardDigits = 
                boardIntArray[1]+boardArray[2]+boardArray[3]+
                boardArray[4]+boardArray[5]+boardArray[6]+
                boardArray[7]+boardArray[8]+boardArray[9];
    	*/
    	boardDigits = 0;
    	for (int i=1 ; i < 10 ; i++) {
    		boardDigits = (int) (boardDigits + boardIntArray[i] * Math.pow(10, (9 - i)));
    		
    	}
    	return boardDigits;
    }
    
    
    
    
    int getGameNumber(){
    	return this.gameNumber;
    }
    
    int switchTurn() {
    	System.out.println("switch turn");
    	if (this.turn == 1) {
    		turn = 2;
    	} else {
    		turn =1;
    	}
    	
    	return turn;
    	
    }
}
