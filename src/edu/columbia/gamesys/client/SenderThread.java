package edu.columbia.gamesys.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SenderThread extends Thread {
	
	SharedData sharedData;
	String lastSentPacket = "Unkonwn lastSentPacket";
	//int lastTargetport = -1;
	
	static String clientName = "Unknown";
	//String myName= clientName;
	static int packetCounter = 0 ;
	static int myport;
	
	static String currentOpponent = "Unknown";
	private boolean offSign = false;
	private boolean sendSign = true;
	
	static int serverport;
    private InetAddress serverIPAddress;
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
    //private int serverport;
 
    public SenderThread(InetAddress address, int clientport, int targetServerport, SharedData sharedData) throws SocketException {
        this.sharedData = sharedData;
    	this.serverIPAddress = address;
        SenderThread.serverport = targetServerport;
        myport = clientport;
        //this.clientport = clientport;
        // Create client DatagramSocket
        this.udpClientSocket = new DatagramSocket(clientport);
        //this.udpClientSocket.connect(serverIPAddress, serverport);
    }
    public void halt() {
        this.stopped = true;
    }
    public DatagramSocket getSocket() {
        return this.udpClientSocket;
    }

    
    public void run() {       
        try {            	
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            while (true) 
            {
                if (stopped)
                    return;
 
                // Message to send
                System.out.println("-what's your next move-");
                String clientInput = inFromUser.readLine();
 
                if (clientInput.equals("."))
                    break;
                String[] parseCToken = clientInput.split(" ");
                String commandC = parseCToken[0];
                String target;
                
                if (parseCToken.length >= 2) {
                target = parseCToken[1].trim();
                //System.out.println("parsed target = "+ target);
                } else target = "no_target" ;
                
                int swC = senderOption (commandC); // Token[0] is command
                String modifiedClientMessage = "Not yet modified by client";
                
                
                  
          		
      			switch (swC) {
      			 case 0: // Resend previous packet
      				 // does not update packetCounter
      				 modifiedClientMessage = lastSentPacket; 
      				 System.out.println("- Resend Packet -");
      				 break;
      			 case 1: //login
      				 //System.out.println("Client loggin in");              				               				 
      				 packetCounter++;
      				 clientName = parseCToken[1].trim();
      				 sharedData.update(clientName);
      				 modifiedClientMessage = "login"+","+ packetCounter +","+ clientName +","+ myport;
      				 lastSentPacket = modifiedClientMessage;              				
      				 
      				 break;
      				 
      			 case 2:              				 
      				 //System.out.println("Client -"+ clientName +" - Quering the List");
      				 packetCounter++;
      				 modifiedClientMessage = "list"+","+ packetCounter +","+ clientName;
      				 lastSentPacket = modifiedClientMessage;
      				 //printMenu();
      				 break;
      				 
      			 case 3:// sending request
      				 //System.out.println("Client - "+ clientName +" Sending Request...");    				 
      				 packetCounter++;
      				 String targetPlayer = target;
      				 modifiedClientMessage = "choose"+","+ packetCounter +","+ clientName+","+ targetPlayer;
      				 lastSentPacket = modifiedClientMessage;
      				 break;
      			
      			 case 31: // accept a request
      				 //System.out.println("accepting request");
      				 packetCounter++;
      				 targetPlayer = target;
      				 modifiedClientMessage = "ackchoose"+","+ packetCounter +","+ clientName+","+ targetPlayer+","+"A";
      				 lastSentPacket = modifiedClientMessage;
      				 break;
      			 case 32:
      				 packetCounter++;
      				 targetPlayer = target;
      				 modifiedClientMessage = "ackchoose"+","+ packetCounter +","+ clientName+","+ targetPlayer+","+"D";
      				 lastSentPacket = modifiedClientMessage;
      				 break;
      			 case 4:
      				 //System.out.println("move");
      				 packetCounter++;
      				 String positionString = parseCToken[1].trim();              				 
      				 modifiedClientMessage = "play"+","+ packetCounter +","+ clientName+","+ positionString ;
      				 lastSentPacket = modifiedClientMessage;
      				 break;
      				 
      			 case 8:
      				 //System.out.println("Client - "+ clientName +" Loggin Out...");
      				 //udpClientSocket.close();
      				 break;
      				 
      			 case 19:
      				 System.out.println("Client test channel");
      				 packetCounter++;
      				 modifiedClientMessage = "testchannel"+","+ packetCounter+","+clientName;
      				 lastSentPacket = modifiedClientMessage;
      				 break;
      				 
      			 case 20:
     				 System.out.println("Client test proxy");
     				 packetCounter++;
     				 serverIPAddress = InetAddress.getByName("192.148.1.2");
     				 serverport = 5000;
     				 modifiedClientMessage = "testproxy"+","+ packetCounter+","+clientName;
     				 lastSentPacket = modifiedClientMessage;
     				 break;	 
      			 case 10:
      				 packetCounter++;
      				 System.out.println(clientName +" logout..");
      				 modifiedClientMessage = "logout"+","+ packetCounter+","+clientName;
      				 lastSentPacket = modifiedClientMessage;
      				 //offSign = true;
      				 
                     //halt();
      				 break;
      			 case 12:
      				 System.out.println("Broadcast..");
     				 
     				 break;
      				 
      			 default:
      				 System.out.println("Invalid Selection");
      				 sendSign = false;
      				 
      				 break;
      				     				 
      				 
      			}// end switch
              			
                if (sendSign == false) {
                	 System.out.println("Please try again");
                } else {
            	//System.out.println("-sending-");              
                byte[] sendData = new byte[4096];                 
                sendData = modifiedClientMessage.getBytes();                 
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport);                 
                System.out.println("-packet to Server ("+serverport +") :"+modifiedClientMessage+" -");
                udpClientSocket.send(sendPacket);
                } // end if sendSign
                                              
               //Reliable Data Trasmission Mechanism  
                 Thread.sleep(700);  // 
                 
                 int counter = 0;
                 int waitLimit = 30; 
                 System.out.println("Sender: currentPacketCounter"+sharedData.getCurrentPacketCounter() );
                  while (packetCounter > sharedData.getCurrentPacketCounter() && counter < waitLimit) {
   					// lost a packet
                	//Thread.yield();
                	  System.out.println("Sender: 2 currentPacketCounter"+sharedData.getCurrentPacketCounter() );
                 	System.out.println("lost a packet"); 
                 	//modifiedClientMessage = lastSentPacket; 
                 	System.out.println("- lastSentPacket :" + lastSentPacket );
     				System.out.println("- Resend Packet -");
     				//packetCounter--;
     				     				
     				byte[] sendData = new byte[4096];                     
                    sendData = modifiedClientMessage.getBytes();                     
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport); 
                    System.out.println("- resend packet to Server ("+ serverIPAddress.toString()+":" +serverport +") :"+modifiedClientMessage+" -");
                    udpClientSocket.send(sendPacket);
   					// resend the message                                        
     				counter++;
                    Thread.sleep(300); 
                    //Thread.yield();
                   }  
                   
   				 }
                
                System.out.println("- Sender: 3 currentPacketCounter"+sharedData.getCurrentPacketCounter() + " - " );
                if (offSign == true) {
                	//udpClientSocket.close();
                	halt();
                	return;
                
                 } 
                Thread.yield();
            
        }
        catch (IOException ex) {
            System.err.println(ex);
        } 
        catch (InterruptedException e) {    
			e.printStackTrace(); 
		}         
    }
    
	// menu system    
    private int senderOption (String commandC){
		int swC = 12;
		
		if (commandC.equalsIgnoreCase("login")) swC = 1; //login
		else if (commandC.equalsIgnoreCase("ls")) swC = 2 ; //Query List
		else if (commandC.equalsIgnoreCase("choose")) swC = 3 ;//send request to a player
		else if (commandC.equalsIgnoreCase("accept")) swC = 31;//accept request
		else if (commandC.equalsIgnoreCase("deny")) swC = 32;
		else if (commandC.equalsIgnoreCase("play")) swC = 4 ;
		else if (commandC.equalsIgnoreCase("x")) swC = 5 ;
		else if (commandC.equalsIgnoreCase("f")) swC = 6 ;
		else if (commandC.equalsIgnoreCase("logout")) swC = 10 ;
		else if (commandC.equalsIgnoreCase("testchannel")) swC = 19 ; //Unknown
		else if (commandC.equalsIgnoreCase("testproxy")) swC = 19 ; 
		else if (commandC.equalsIgnoreCase("broadcast")) swC = 12;//broadcast
		return swC;
	} 
}   
