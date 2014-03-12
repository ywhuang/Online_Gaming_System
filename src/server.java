
/*****
  *  Project - Online gaming System
  * @author YiweiHuang
  *
  *
  * UNI: yh2565
  * email: yh2565@columbia.edu
  * 
  * Note:
  * Please run clients and the server on the same machine.
  * Serverport is set to 4119.
  * 
  * To Initiate client:
  * java client <clientport> <serverIP> <serverport>
  * 
  * To Initiate server:
  * java server
  * 
  * 
  *****/
import java.io.IOException;
import java.net.*; // Imported because the Socket class is needed
import java.util.TreeMap;
//import java.util.HashSet;
 
public class server {	
 
	private static ST<String, String[]> st = new ST<String, String[]>();
	 //    index|    0        1         2           3         4        5              6        7
	 // st value: {<port>, <state>, <IP address>,<opponent>,<game>,< player1 or 2>,<reserv>,<reserv>}
	private static ST<String, Integer> portDataTree = new ST<String, Integer>();	
	private static ST<String, String> opponentDataTree = new ST<String, String>();
	private static ST<String, String> statusDataTree = new ST<String, String>();
	private static ST<String, Integer> gameNumberTree = new ST<String, Integer>();
	
	private static TreeMap<String, Integer> ipCountDataTree = new TreeMap<String, Integer>();
		
	static Game[] games = new Game[50]; //Max 50 games
	static int gameCounter = 0;
	
	
	
	public static int transS(String commandS) {
		
		int swS = 15; //if command unknown
		if (commandS.equalsIgnoreCase("login")) swS = 1; //Login
		else if (commandS.equalsIgnoreCase("list")) swS = 2 ; //Query List
		else if (commandS.equalsIgnoreCase("choose")) swS = 3 ;//Choose player
		else if (commandS.equalsIgnoreCase("ackchoose")) swS = 31 ;//accept request from server
		else if (commandS.equalsIgnoreCase("deny")) swS = 32 ;
		else if (commandS.equalsIgnoreCase("play")) swS = 4 ;
		else if (commandS.equalsIgnoreCase("logout")) swS = 10 ;
		else if (commandS.equalsIgnoreCase("testgame")) swS = 9 ;
		else if (commandS.equalsIgnoreCase("testchannel")) swS = 19 ;
		else if (commandS.equalsIgnoreCase("error")) swS = 16 ;
		else if (commandS.equalsIgnoreCase("testproxy")) swS = 20 ;
		else if (commandS.equalsIgnoreCase("Not yet modified by Client")) swS = 12 ;
		
		System.out.println("checkpointswS swS = " + swS + ", command= " + commandS );
		return swS ;
		}
	
	  private static void sendMessage(DatagramSocket serverSocket, String modifiedReturnMessage, InetAddress clientIP, int targetport) throws IOException {
		
		byte[] sendData  = new byte[4096];            			
		sendData = modifiedReturnMessage.getBytes();						       
		System.out.println("To client - "+ targetport+ " : "+modifiedReturnMessage);		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, targetport);						
		// Send the echoed message          
		serverSocket.send(sendPacket);  
	  }
 
	public static void main(String args[]) throws Exception {
 

        int serverport = 4119;  //Default server port
        int targetport;
        int packetId = -1;
        boolean sendPermission = true;
        
         System.out.println("checkpoint1"); 
        
        if (args.length < 1) {
            System.out.println("Usage: UDPServer " + "Now using Port# = " + serverport);
        }         
        else {            
            serverport = Integer.valueOf(args[0]).intValue();
            System.out.println("Usage: UDPServer " + "Now using Port# = " + serverport);
        }
 
	    // Open a new datagram socket on the specified port
	    DatagramSocket udpServerSocket = new DatagramSocket(serverport);         
	    System.out.println("Server started...\n");
	    System.out.println("checkpoint2");
	     
	    while(true)
		{			
	    	System.out.println("checkpoint3");
	    	
			byte[] receiveData = new byte[4096];          
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
			// Block until there is a packet to receive, then receive it
			udpServerSocket.receive(receivePacket);           
			String clientMessage = (new String(receivePacket.getData())).trim();
 
			// Print some status messages
			System.out.println("Client Connected - Socket Address: " + receivePacket.getSocketAddress());
			System.out.println("received packet: [" + clientMessage + "]");          			
			//InetAddress clientIP = receivePacket.getAddress();
			InetAddress clientIP = InetAddress.getByName("localhost"); // do not change
			InetAddress clientRealIP = receivePacket.getAddress(); // used for proxy server
			
			// Print out status message
			System.out.println("Client IP Address & Hostname: " + clientIP + ", " + clientIP.getHostName() + "\n");
 			
			int clientport = receivePacket.getPort();
		
			System.out.println("ClientRealIP toString: "+clientRealIP.toString());
			String clientRealIPString = clientRealIP.toString(); 
		
			// Response message					
			String[] parseTokens = clientMessage.split(",");
            String command = parseTokens[0];
            String user;
            if (parseTokens.length < 3){
          	  user = "no user identified in the coming packet";
            } else {
            packetId = Integer.parseInt(parseTokens[1].trim());
            user = parseTokens[2].trim();
            }
         
            int swS = transS(command);
            
            String modifiedReturnMessage = "default error message";
            targetport = clientport; // default to originate
            
            String modifiedReturnMessage3 = "unmodified ack"; //ack msg
            int targetport3 = -1;//ack target port           
			
			
            switch (swS) {
			 case 1: //login
				 //Packet Content: [ login,<Id>,<name>,<port> ]
      				      								 				
				 int userport = Integer.parseInt(parseTokens[3]);				
				 String userName = parseTokens[2].trim();
				 String status = "Unknown";
				 String ackStatus = "UnKnown";
				//check st.contatins to determine <status>				 
				 //send ack    
		            modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
		            targetport3 = clientport; // default targetport
		            byte[] sendData3  = new byte[1024];            			
					sendData3 = modifiedReturnMessage3.getBytes();						       
					System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
					DatagramPacket sendPacket3 = new DatagramPacket(sendData3, sendData3.length, clientRealIP, targetport3);																			       
					udpServerSocket.send(sendPacket3);  
				// end sending ack //
					
					//check ipCount
				System.out.println("checkpointSameIP limits");
				//========  IP Detection Mechanism ======
				
				// record ip
				
				if (ipCountDataTree.containsKey(clientRealIPString)) {
					int ipCount = ipCountDataTree.get(clientRealIPString);
					ipCount++;
					ipCountDataTree.put(clientRealIPString, ipCount);
				} else {
					ipCountDataTree.put(clientRealIPString, 1);
				}								
				
				//======== IP Detection Mechanism =====
				if (ipCountDataTree.get(clientRealIPString) > 5 ) {
						// redirect IP
						System.out.println("more than 5 clients from same IP!!");
						ackStatus = "F" ; // login fail
						
						System.out.println("Login Failed - "+ userName+"IP limits");
						targetport = userport;
						ackStatus = "F" ;
						modifiedReturnMessage = "acklogin"+","+ackStatus;
						break;
				}
				 //duplicate ports detection
				 for (String s : portDataTree.keys()) {
					 if (portDataTree.get(s) == userport) {
						 System.out.println("Duplicate Ports. Please choose another port");
						 ackStatus = "F";
						 targetport = userport;
						 modifiedReturnMessage = "acklogin"+","+ackStatus;
						 break;
					 }
				 }
				 
				 if (st.contains(userName)){
					 // do nothing if user already existed
					ackStatus = "F" ; // login fail
					System.out.println("st contains " + userName);
					System.out.println("Login Failed - "+ userName+" exists");
					modifiedReturnMessage = "acklogin"+","+ackStatus;
					targetport = userport;
				 } 						 
					 			
				 else {
				 ackStatus = "S"; // login success
				// st value: {<port>, <state>, <IP address>,<opponent>,<game>,< reserv>,<reserv>,<reserv>}
				 String[] userData = { parseTokens[3],"stateX", "IPaddressX" , "opponentX","gameX","reserv","reserv","reserv" };
				 st.put(userName, userData);
				 statusDataTree.put(userName, "free");
				 st.get(userName)[1] = "free";
				 portDataTree.put(userName, userport);
				 System.out.println("Login Success - " + userName);
				 targetport = userport;
				 }

				 modifiedReturnMessage = "acklogin"+","+ackStatus;
				 
				 break;
				 
			 case 2://query list - [ list,<Id>,<userName> ]
				System.out.println("current online players: ") ;
				if (parseTokens.length < 3) {
					userName = "NouserName";
					modifiedReturnMessage = "ack,error,error";
					break;
				} else {
				  userName = parseTokens[2].trim();
				}
				//send ack    //
	            modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
	            targetport3 = clientport; // default targetport
	            byte[] sendData32  = new byte[1024];            			
				sendData32 = modifiedReturnMessage3.getBytes();						       
				System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
				DatagramPacket sendPacket32 = new DatagramPacket(sendData32, sendData32.length, clientRealIP, targetport3);																			       
				udpServerSocket.send(sendPacket32);  
			    // end sending ack  //
				
				
				String userList = "";
				for (String key : statusDataTree.keys()){
					userList = userList + "," + key + "," + statusDataTree.get(key);
				}
				//targetport = clientport;
				//userName = parseTokens[2].trim();
				targetport = portDataTree.get(userName);
				
				modifiedReturnMessage = "ackls"+ userList;
				System.out.println("List: " + userList);
												
				break;
				 
			  case 3:			
				 // choose   - [ choose,<Id>,<userName>,<chosenName>] 
				  userName = parseTokens[2].trim();
				    //send ack    //
		            modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
		            targetport3 = clientport; // default targetport
		            byte[] sendData33  = new byte[1024];            			
					sendData33 = modifiedReturnMessage3.getBytes();						       
					System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
					DatagramPacket sendPacket33 = new DatagramPacket(sendData33, sendData33.length, clientRealIP, targetport3);																			       
					udpServerSocket.send(sendPacket33);  
				    // end sending ack - //
					
				 String targetx = parseTokens[3].trim();				 
				 if (st.contains(targetx)){
					 System.out.println("st dataTree found the client");
					 if (statusDataTree.get(targetx).equals("free") ){
						 System.out.println("and the client is free");
						 String temp = st.get(targetx)[0];			     		     
						 targetport = Integer.parseInt(temp.trim());
						 modifiedReturnMessage = "request"+","+user;
						 
						 // decision status update
						 statusDataTree.put(userName, "decision");
						 
					 } else {
						 System.out.println("but the client is busy");
						 			     		     
						 targetport = portDataTree.get(userName);
						 modifiedReturnMessage = "ackchoose"+","+targetx+","+"F";
					 }
					 

				 } else {
					 
					 System.out.println("Sever couldn't find the client in st dataTree");
					 targetport = portDataTree.get(userName);
					 modifiedReturnMessage = "ackchoose"+","+targetx+","+"F";
					
				} //end else

				 break;
				 
			  case 31:
				  //  <accept & deny>          0              1                  2               3            4
				  // incoming message= "ackchoose"+","+ packetCounter +","+ clientName+","+ targetPlayer+","+"A";
				  //System.out.println("infrom acceptance");
				  userName = parseTokens[2].trim();
				  //send ack   - //
				  
				  modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
		  		  targetport3 = clientport; // default targetport
		          byte[] sendData331  = new byte[1024];            			
		  		  sendData331 = modifiedReturnMessage3.getBytes();						       
		  		  System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
		  		  DatagramPacket sendPacket331 = new DatagramPacket(sendData331, sendData331.length, clientRealIP, targetport3);																			       
		  		  udpServerSocket.send(sendPacket331);  
		  		  // end sending ack  //
		  		  
				  String targety = parseTokens[3];
				  String ackStatusReceived = parseTokens[4].trim();
				  
				  if (ackStatusReceived.equals("D") ) {
					  // deny
					  
					  
					  String targetz = parseTokens[3];
					  System.out.println(userName + " rejected " + targetz);	
					  if (st.contains(targetz)) {						    
						     String tempy = st.get(targetz)[0];						    			    
							 targetport = Integer.parseInt(tempy.trim());
							 modifiedReturnMessage = "ackchoose"+","+userName+","+"D";
							 
					  } else {						 
							 System.out.println("can't find "+ targetz +"'s data in st");

							 if ( portDataTree.contains(targetz)) {
								 targetport = portDataTree.get(targetz);
								 System.out.println("found in portDataTree");
								 System.out.println("targetport = " + targetport);
							 }

							 modifiedReturnMessage = "ackchoose"+","+userName+","+"F";
					  }
					  
					  break;
					  
				  } // end if "D"
				  
				  System.out.println(user + "accept" + targety);				  				  				  				  
				  modifiedReturnMessage = "ackchoose"+","+user+","+"A";
				  if (st.contains(targety)) {					
					     String tempy = st.get(targety)[0];
	     
						 targetport = Integer.parseInt(tempy.trim());
						 
				  } else {						 
						 System.out.println("can't find "+ targety +"'s data in st");

						 
						 if ( portDataTree.contains(targety)) {
							 targetport = portDataTree.get(targety);
							 System.out.println("found in portDataTree");
							 System.out.println("targetport = " + targetport);
						 } 	
						 targetport = portDataTree.get(userName);
						 modifiedReturnMessage = "ackaccept"+","+targety+","+"F";
						 break;
						 
				  }				 
				  
				  // create players
				  Player newplayer1 = new Player(targety, targetport, 1);  // the one who requests first first
				  Player newplayer2 = new Player(user, portDataTree.get(userName), 2 );   // the one who accepts second
				  newplayer1.opponentport = newplayer2.playerport; // record opponent port
				  newplayer2.opponentport = newplayer1.playerport;
				  
				  // update userDataTree
                  statusDataTree.put(user,"busy");
                  statusDataTree.put(targety,"busy");
                  
                  st.get(user)[1] = "busy";
                  st.get(targety)[1] = "busy";
				  st.get(user)[5] = "2"; 
				  st.get(targety)[5] = "1";
				 
				  //create a new game
				  Game newGame = new Game(newplayer1, newplayer2, gameCounter);
				  
				  System.out.println("check player1 name:"+ newGame.player1.name );
				  System.out.println("check player2 name:"+ newGame.player2.name );
				  
				  System.out.println("check player1 port:"+ newGame.player1.playerport );
				  System.out.println("check player2 port:"+ newGame.player2.playerport );
				  
				  opponentDataTree.put(user, targety);
				  opponentDataTree.put(targety, user);
				  
				  gameNumberTree.put(targety, gameCounter);
				  gameNumberTree.put(user, gameCounter);				 
				  
				  games[gameCounter] = newGame;
				  gameCounter++;
				  System.out.println("new game created");
				  modifiedReturnMessage = "play" + ", " + newGame.displayEmptyBoardDigits();
				
				  break;
				  
			  case 32: //deny     ackchoose, <id>, <userName>, <deniedName>, <status>
				  
				  userName = parseTokens[2].trim();
				  //send ack   //				  
				  modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
		  		  targetport3 = clientport; // default targetport
		          byte[] sendData332  = new byte[1024];            			
		  		  sendData3 = modifiedReturnMessage3.getBytes();						       
		  		  System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
		  		  DatagramPacket sendPacket332 = new DatagramPacket(sendData332, sendData332.length, clientRealIP, targetport3);																			       
		  		  udpServerSocket.send(sendPacket332);  
		  		  // end sending ack  //
		  		  
				  String targetz = parseTokens[3];
				  System.out.println(userName + "deny" + targetz);	
				  if (st.contains(targetz)) {
					     //System.out.println("Sever  found" + targety);
					     String tempy = st.get(targetz)[0];
					     //System.out.println("tempy = " + tempy);			     
						 targetport = Integer.parseInt(tempy.trim());
						 
				  } else {						 
						 System.out.println("can't find "+ targetz +"'s data in st");
						 //System.out.println("now try portDataTree");
						 
						 
						 if ( portDataTree.contains(targetz)) {
							 targetport = portDataTree.get(targetz);
							 System.out.println("found in portDataTree");
							 System.out.println("targetport = " + targetport);
						 }
				  
				  //targetport = portDataTree.get(userName); redundunt
				  modifiedReturnMessage = "ackchoose"+","+userName+","+"F";
				  }
				  
				  break;
				  
			  case 4:  //play     receive paket contet: play,<packetID>,<user>,<positioin number>
				  
				  int position = Integer.parseInt(parseTokens[3]);
				  //System.out.println("parse position : " + position );
				  userName = parseTokens[2].trim();
				  
				  //send ack   - ack-  -ack- -ack- - ack-  -ack- -ack- //				  
				  modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
		  		  targetport3 = clientport; // default targetport
		          byte[] sendData34  = new byte[1024];            			
		  		  sendData34 = modifiedReturnMessage3.getBytes();						       
		  		  System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
		  		  DatagramPacket sendPacket34 = new DatagramPacket(sendData34, sendData34.length, clientRealIP, targetport3);																			       
		  		  udpServerSocket.send(sendPacket34);  
		  		  // end sending ack - ack-  -ack- -ack- - ack-  -ack- -ack- //
				  
				  //load game				  
				  int currentGameNumber = gameNumberTree.get(user);
				  Game currentGame = games[currentGameNumber];
				  Player currentPlayer;
				  //int playerNumber = -1;
				  System.out.println("user :"+user );
				  String player1Name = currentGame.player1.name;
				  String player2Name = currentGame.player2.name;
				  System.out.println("currentGame.player1.name:"+ currentGame.player1.name );
				  System.out.println("currentGame.player2.name:"+ currentGame.player2.name );
				  //check if current player is 1 or 2
				  if (user.equals(currentGame.player1.name) ){
					  
					  currentPlayer = currentGame.player1;
					  System.out.println(user + " is player 1");
				  } else if (user.equals(currentGame.player2.name) ){
					  
					  currentPlayer = currentGame.player2;
					  System.out.println(user + " is player 2");
				  } else {
					  System.out.println("cannot find " + user + " in game " + currentGameNumber);
					  modifiedReturnMessage = "case 4 - cannot find " + user ;
					  break;
				  }
				  
				  //check user is player 1 or 2
				  //check turns
				  if (currentGame.turn == currentPlayer.playerNumber ) {	
					  System.out.println("Valid turn" );
					  // valid turn
				  } else {
					  // invalid turn
					  System.out.println("Invalid turn" );
					  modifiedReturnMessage = "ackplay"+","+"T"; //Out-of-Turn  
					  targetport = portDataTree.get(userName);
					  break;
				  }
				  //Check Oppupied
				  if (currentGame.judgeView.contains(position)) {
					  System.out.println("Oppcupied" );
					  modifiedReturnMessage = "ackplay"+","+"O"; //Occupied
					  targetport = portDataTree.get(userName);
					  break;
				  }
				  
				  //Check win
					  // check which player plays current game
					  if (currentGame.player1.equals(currentPlayer) ) {
						  //player 1 here
						  currentGame.player1Move(position);
						  System.out.println("player 1 moved");
						  targetport = currentGame.player2.playerport; // msg targets to opponent
						  //check winning move?
						  if (currentGame.checkWin(currentGame.player1Record)) {
							  // winning
							  System.out.println("player 1 -"+currentPlayer.name +" won");
							//currentGame.gameOver = true;
							  //currentGame.switchTurn();
							  
							  //inform winner
                              int targetportTemp = portDataTree.get(userName);
                              InetAddress targetIPTemp= clientIP;
                              String modifiedReturnMessageTemp = "result"+","+"W";
                              sendMessage(udpServerSocket, modifiedReturnMessageTemp, targetIPTemp, targetportTemp);
							  //inform loser
							  currentGame.winner = currentPlayer;	
							  modifiedReturnMessage = "result"+","+"L";//send Lose Message to opponent
							  
							  //updata data
							  statusDataTree.put(player1Name,"free");
			                  statusDataTree.put(player2Name,"free");
							  
							  break;
						  }
						  
						  
					  } else {
						  //player 2 here
						  currentGame.player2Move(position);
						  targetport = currentGame.player1.playerport; // msg targets to opponent
						  
						 //check winning move?
						  if (currentGame.checkWin(currentGame.player2Record)) {
							  System.out.println("player 2 -"+currentPlayer.name +" won");
							  //currentGame.gameOver = true;
							  //currentGame.switchTurn();
							  //inform winner
							  int targetportTemp = portDataTree.get(userName);
                              InetAddress targetIPTemp= clientIP;
                              String modifiedReturnMessageTemp = "result"+","+"W";
                              sendMessage(udpServerSocket, modifiedReturnMessageTemp, targetIPTemp, targetportTemp);
							  //inform loser
							  currentGame.winner = currentPlayer;
							  modifiedReturnMessage = "result"+","+"L";//send Message to opponent
							  statusDataTree.put(player1Name,"free");
			                  statusDataTree.put(player2Name,"free");
							  
							  break;
						  }
					  }
					  // end move
					  
					  //check draw
					  if (currentGame.isFull()) {
						  int targetportTemp = portDataTree.get(userName);
                          InetAddress targetIPTemp= clientIP;
                          String modifiedReturnMessageTemp = "result"+","+"D";
                          sendMessage(udpServerSocket, modifiedReturnMessageTemp, targetIPTemp, targetportTemp);
						  
                          //inform opponent						  
                          targetport = currentPlayer.opponentport;
						  modifiedReturnMessage = "result"+","+"D";
						  
						  //update data
						  statusDataTree.put(player1Name,"free");
		                  statusDataTree.put(player2Name,"free");
		                  st.get(player1Name)[1] = "free";
		                  st.get(player2Name)[1] = "free";
						  break;
					  }
					  
					  // continue to play
					  if (currentGame.gameOver == false) {
					  System.out.println("displayBoardDigits : " + currentGame.displayBoardDigits() );
					  modifiedReturnMessage = "play"+","+currentGame.displayBoardDigits();					  					  
					  currentGame.switchTurn();
					  System.out.println("switch turn to " + currentGame.turn );
					  } 
					  
				  
				  break;
				  
				  
			  
				 
			  case 9:
				  System.out.println("testgame..");
				  Player player1 = new Player("test1", 7788,1);
				  Player player2 = new Player("test2", 7799,2);
				  Game game1 = new Game(player1, player2, 5); // game 5
				  String gameStatus = game1.displayEmptyBoard();
				  modifiedReturnMessage =gameStatus;
				  targetport = clientport;
				  
				  break;
			  case 10:  // logout, id , <userName2>
				     String userName2 = parseTokens[2].trim();
				     targetport = portDataTree.get(userName2);
					 //System.out.println("userName - " + userName);
				     String ackStatus2 = "unknown";
					//check st.contatins to determine <status>
				     
					  //send ack    //				  
					  modifiedReturnMessage3 = "ack"+","+String.valueOf(packetId);
			  		  targetport3 = clientport; // default targetport
			          byte[] sendData310  = new byte[1024];            			
			  		  sendData310 = modifiedReturnMessage3.getBytes();						       
			  		  System.out.println("Server now sending ack to client - "+ targetport3+ " : "+modifiedReturnMessage3);					
			  		  DatagramPacket sendPacket310 = new DatagramPacket(sendData310, sendData310.length, clientRealIP, targetport3);																			       
			  		  udpServerSocket.send(sendPacket310);  
			  		  // end sending ack //
					 if (st.contains(userName2) && statusDataTree.contains(userName2)){
						 st.delete(userName2);
						 statusDataTree.delete(userName2);
						 
						 if (portDataTree.contains(userName2)){
						 portDataTree.delete(userName2);}
						 if (opponentDataTree.contains(userName2)){
						 opponentDataTree.delete(userName2);}
						 
						 //ackStatus2 = "F" ; // login fail
						 modifiedReturnMessage = "logout success";						
					 } else {
					 
					 
					 System.out.println("Login out Failed - " + userName2);
					 modifiedReturnMessage = "logout fail";
					 }
					 //System.out.println("Sending Login Ack...");
					 
					 modifiedReturnMessage = "logout success";
					 
					 break;
				  
			  case 12:
				  System.out.println("check Client encoder");
				  break;
				  
			  case 15:
					 System.out.println("Unkonwn Command");
					 sendPermission = false;
					 //serverSocket.close();
					 break;
			  case 16:
					 System.out.println("error Message");
					 sendPermission = false;
					 //serverSocket.close();
					 break;
				  
			  case 19:
			  	  System.out.println("test channel");
			  	 targetport = 5000;
				 modifiedReturnMessage = "ack, 99, from real server 4119 to channel";
				 break;
			  case 20:
				  System.out.println("test proxy");
				  targetport = 5000;
				  modifiedReturnMessage = "ack, 99, from real server 4119 to proxy";
			    break;
			    
			 default:
				 System.out.println("Invalid Selection - please try again");
				 //printMenu();
				 break;
				     				 
            	 
			} 
			
			//========
			
          
			// Create an empty buffer/array of bytes to send back 
            if (sendPermission = false) {
            	System.out.println("sendPermission is false. No message sent");
            } else
            {
			byte[] sendData30  = new byte[4096];            			
			sendData30 = modifiedReturnMessage.getBytes();						       
			System.out.println("Server to client - "+ clientIP +" "+ targetport+ " : "+modifiedReturnMessage);			
			DatagramPacket sendPacket = new DatagramPacket(sendData30, sendData30.length, clientIP, targetport);
			
			
			
			// Send the echoed message          
			udpServerSocket.send(sendPacket);  
			
            } 
            	
            
			/***
			//BraodCast
			 * 
			System.out.println("Sending broadcast");
			byte[] broadcastData = new byte[4096];
			String broadcastMessage = "broadcast,nothing";
			broadcastData = broadcastMessage.getBytes();
			for(Integer port : portSet) 
			{
				System.out.println(port != clientport);
				if(port != clientport) 
				{   
					
					// Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port
					DatagramPacket broadcastPacket = new DatagramPacket(broadcastData, broadcastData.length, clientIP, port); 
					System.out.println("Sending broadcast");
					// Send the echoed message          
					udpServerSocket.send(broadcastPacket);    
				}
			}
			***/
        }
    }
       
	
	
}