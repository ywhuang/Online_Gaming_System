package edu.columbia.gamesys.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ReceiverThread extends Thread {
    
	SharedData sharedData;
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
    private boolean offSign2 = false;
    int packetIdCheck = -1;
    int receiverServerpot = 1;
 
    public ReceiverThread(DatagramSocket ds, SharedData sharedData,
    		     InetAddress receiverServerAddress ,int receiverServerport) throws SocketException {
        this.udpClientSocket = ds;
        this.sharedData = sharedData;
        //this.receiverServerpot = 4119;
        //this.udpClientSocket.connect(receiverServerAddress, receiverServerport);
    }
 
    public void halt() {
        this.stopped = true;
    }
    private int recOption (String commandR){
		int swC = 12;
		
		if (commandR.equalsIgnoreCase("ack")) swC = 0 ;
		else if (commandR.equalsIgnoreCase("acklogin")) swC = 1; //login
		else if (commandR.equalsIgnoreCase("ackls")) swC = 2 ; //Query List
		else if (commandR.equalsIgnoreCase("ackchoose")) swC = 31 ;//send request to a player
		else if (commandR.equalsIgnoreCase("request")) swC = 3;//accept request
		else if (commandR.equalsIgnoreCase("play")) swC = 4;
		else if (commandR.equalsIgnoreCase("ackplay")) swC = 41 ;
		else if (commandR.equalsIgnoreCase("result")) swC = 8 ;
		else if (commandR.equalsIgnoreCase("xxx")) swC = 6;		
		else if (commandR.equalsIgnoreCase("testchannel")) swC = 19 ; //Unknown
		else if (commandR.equalsIgnoreCase("logout")) swC = 10 ;// logout response
		else if (commandR.equalsIgnoreCase("broadcast")) swC = 12;//broadcast
		return swC;
	}
 
    public void run() {
 
        // Create a byte buffer/array for the receive Datagram packet
        byte[] receiveData = new byte[4096];
        System.out.println("-receiving packet- receiverport@" + udpClientSocket.getPort());
        while (true) {            
            if (stopped)
            return;
            
           
            
            // Set up a DatagramPacket to receive the data info
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            //System.out.println("-receving-");
            try {
                // Receive a packet from the server (blocks until the packets are received)
                udpClientSocket.receive(receivePacket);
                System.out.println("-receiving packet- receiverport@" + udpClientSocket.getPort());
                // Extract the reply from the DatagramPacket      
                String serverReply =  new String(receivePacket.getData(), 0, receivePacket.getLength());
                
                // print to the screen
                System.out.println("- packet from Server (" + receivePacket.getAddress().toString() 
                		                   + receivePacket.getPort() + " ) : \"" + serverReply + "\" - \n");
                String[] parseRToken = serverReply.split(",");
                String commandR = parseRToken[0];
                int swR = recOption (commandR); // Token[0] is command
                //String modifiedDisplayMessage = "Intended to display: Not yet modified by client Receiver";
                
                  
          		
      			switch (swR) {
      			 case 0:
     				 System.out.println("-receiver: ack received-");
     				 if (parseRToken.length > 2) {
     					 
     					System.out.println("-received ack error - ");
     				 } else {
     				 int receivedCounter = Integer.parseInt(parseRToken[1].trim()); //error cuz "1"
     				 System.out.println("-receivedCounter :" + receivedCounter +" - " );
     				 sharedData.updateCurrentPacketCounter(receivedCounter);
     				 }
     				 System.out.println("- currentPacketCounter: " + sharedData.getCurrentPacketCounter() +" -");
     				 break;
      			 
      			 case 1:
      				 
      				 String loginStatus = parseRToken[1].trim();
      				   if (loginStatus.equals("S")) {
      					 System.out.println("login success " + sharedData.getName());
      				   } else if (loginStatus.equals("F")) {
      					 System.out.println("login Fail" + sharedData.getName());
      				   }
      				 break;
      				 
      			 case 2://list
      				 
      				String userStatus;              				
      				for (int i=1;i< ( parseRToken.length ) ; i= i +2){
      			  
      				userStatus= parseRToken[i].trim() + " " + parseRToken[i+1].trim();
      				System.out.println(userStatus);
      				}
      				System.out.println("EOL");
      				 
      				 break;
      				 
      			 case 3://request
      				 String requestedUser = parseRToken[1].trim();
      				 System.out.println(serverReply);
      				 System.out.println("request from "+ requestedUser);
      				 break;
      				 
      			 case 4: // send <play> header
      				 //for (i=1;i<parseRToken.length;i++){
      				String gameStatus = parseRToken[1].trim();
      				if (gameStatus.equals("000000000")) {
      					
      					System.out.println("\n"+"_ "+"_ "+"_ "+"\n"+"_ "+"_ "+"_ "+"\n"+"_ "+"_ "+"_ ");
      					
      				} else {
      				//System.out.println(gameStatus);      				
      				//String gameGrid = "\n";
      				
      				int number = Integer.valueOf(gameStatus); // = and int
      				
      				int[] digit = new int[10];
      				digit[0] = 8;
      				digit[1] = number  / 100000000;
      				digit[2] = (number % 100000000) /  10000000;
      				digit[3] = (number % 10000000 ) /  1000000;
      				digit[4] = (number % 1000000  ) /  100000;
      				digit[5] = (number % 100000   ) /  10000;
      				digit[6] = (number % 10000    ) /  1000;
      				digit[7] = (number % 1000     ) /  100;
      				digit[8] = (number % 100      ) /  10;
      				digit[9] = (number % 10       ) /  1;
      				
      				System.out.println(" ");
      				for (int i=1; i<10 ; i ++) { 
        				  if (digit[i] == 1) System.out.print("1 ");
        				  if (digit[i] == 2) System.out.print("2 ");
        				  if (digit[i] == 0) System.out.print("_ ");
        				  if (i == 3 || i == 6 || i == 9) System.out.println("");
        					  //gameGrid = gameGrid + "\n";
        				   }
      				
      				}
     				 
     				 break;
     				 
      			 case 41://send <ackplay> header
      				 String ackplayStatus = parseRToken[1].trim();
     				 System.out.println(serverReply);
     				if (ackplayStatus.equals("O")) {
     					 System.out.println("Occupied");
     				 } else if (ackplayStatus.equals("T")) {
     					 System.out.println("Out of turn");
     				 }
     				 
      				 
      				 break;
      			 case 31: //send ackchoose header
      				 String targetUser = parseRToken[1].trim();
      				 String chooseStatus = parseRToken[2].trim();
      				 System.out.println(serverReply);
      				 if (chooseStatus.equals("A")) {
      					 System.out.println("request accepted by "+targetUser);
      				 } else if (chooseStatus.equals("F")) {
      					 System.out.println("request to " + targetUser + " failed");
      				 } else if (chooseStatus.equals("D")) {
      					System.out.println("request denied by "+targetUser);
      				 }
      			      break;
      			      
      			 case 8: //display the game result
      				 String result = "unKnown";
      				 result = parseRToken[1].trim();
     				 if (result.equals("W")) {
     					System.out.println(sharedData.getName()+" Win");
     				 } else if (result.equals("L")) {
      					System.out.println(sharedData.getName()+" Lose");
      				 } else if (result.equals("D")) {
      					System.out.println(sharedData.getName()+" Draw");
      				 }
      				 
     				 break;
     				 
      			 case 19:
      				 System.out.println("test channel - receiver");
      				 break;
      			 case 10:
      				 System.out.println("-receiver :log out -");
      				 offSign2 = true;
      				 udpClientSocket.close();
      				 halt();
                                   				 
      				 return;
      				 
      			}
                Thread.yield(); 
            } 
            catch (IOException ex) {
            System.err.println(ex);
            }
        }
    }
}
