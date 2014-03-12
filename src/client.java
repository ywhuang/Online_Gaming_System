import java.io.*;  // Imported because we need the InputStream and OuputStream classes
import java.net.*; // Imported because the Socket class is needed
import java.util.LinkedList;
/*****
 * Online Gaming System
 * @author  YiweiHuang
 *
 * email: yh2565@columbia.edu
 * 
 * 
 *
 * 
 ******/

public class client {
	
	//public static String myName;
	//public void passName(String myName) {
	//	this.myName = myName;
	//}
	
	
	
    public static void main(String args[]) throws Exception {  
 
        // The default port     
        int clientport = 7788;
        String host = "localhost"; // server address
        String serverReceiverHost = "localhost"; //server address for receiver
        InetAddress receiverServerIp = InetAddress.getByName( serverReceiverHost);
        int targetServerport = 4119; // default severport
        int receiverServerport = 4119;
        
        
        SharedData sharedData = new SharedData();  // a common data to be accessed by sender and receiver threads
        
        if (args.length < 1) {
           System.out.println("Message: Client " + "now using host = " + host + ", Port# = " + clientport);
        } 
        // Get the port & host number to use from the command line
        else if (args.length > 2){ 
           if (args[1] != null) {
              host = args[1];}  //new server address
           
           if (args[2] != null) {
        	  targetServerport = Integer.valueOf(args[2]).intValue();
        	
           } 
           clientport = Integer.valueOf(args[0]).intValue();
           System.out.println("Message: Client " + "now using host = " + host + ", Port# = " + clientport);
        } else {
        	clientport = Integer.valueOf(args[0]).intValue();
            System.out.println("Message: Client " + "now using host = " + host + ", Port# = " + clientport);
        }
 
        // Get the IP address of the local machine - we will use this as the address to send the data to
        InetAddress ia = InetAddress.getByName(host); // change proxy IP here
        
        // Start both threads together ( with one Sharedata instance)
        
        SenderThread sender = new SenderThread(ia, clientport, targetServerport,sharedData);
        sender.start();
        System.out.println("Message: Client Receiver " + "now listens to IP = " + host + ", Port# = " + clientport);
        ReceiverThread receiver = new ReceiverThread(sender.getSocket(),sharedData,
        		                           receiverServerIp, receiverServerport);
        receiver.start();
    }
}

class SharedData { // shared data for both threads
	
	   String myName = "unKnown";
	   boolean valueSet = false;
	   boolean packetReceived = false;
	   int currentPacketCounter = 0;  // Or Nonce I mean.
	   String cachedMessage ="Not yet cached";
	   
	   //synchronized  ( this works without using synchornized method, perhaps cause using sleep() instead)
	   int getCurrentPacketCounter(){
		   return this.currentPacketCounter;
	   }
	   
	   //synchronized 
	   void updateCurrentPacketCounter(int receivedCounter) {
		   this.currentPacketCounter = receivedCounter;
	   }
	   
	   synchronized boolean isPacketReceived(){
		   
		   return this.packetReceived;
	   }
	   
	   synchronized void updatePacketReceived() {
		   
		   this.packetReceived = true;
	   }
	   
	   synchronized String getName() {
	     
	      return myName;
	   }
	   synchronized void update(String myName) {
		     
		      this.myName = myName;
		      
		   }
}

 
class SenderThread extends Thread {
	
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
    static int transC (String commandC){
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
	} // end transC () 
    public void run() {       
        try {    
        	//send one time blank message
        	
        	/*
        	byte[] data = new byte[4096];
        	data = "ping,".getBytes();
        	DatagramPacket blankPacket = new DatagramPacket(data,data.length , serverIPAddress, serverport);
            udpClientSocket.send(blankPacket);
            */
        	// Create input stream
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
                
                int swC = transC (commandC); // Token[0] is command
                String modifiedClientMessage = "Not yet modified by client";
                
                
                  
          		
              			switch (swC) {
              			 case 0: // resend previous packet
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
              				 //lastTargetport = targetport;
              				 
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
              				 //int position = Integer.parseInt(positionString);
              				 //System.out.println("parsed position = " + position);
              				 //String Opponent = ;
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
                
               
               //anti-lost packet mechanism
               //****** Packet Loss Prevention Mechanism  
                 Thread.sleep(700);  // 
                 
                 int safeCounter = 0;
                 int safeN = 30; 
                 System.out.println("Sender: currentPacketCounter"+sharedData.getCurrentPacketCounter() );
                  while (packetCounter > sharedData.getCurrentPacketCounter() && safeCounter < safeN) {
   					// lost a packet
                	//Thread.yield();
                	  System.out.println("Sender: 2 currentPacketCounter"+sharedData.getCurrentPacketCounter() );
                 	System.out.println("lost a packet"); 
                 	//modifiedClientMessage = lastSentPacket; 
                 	System.out.println("- lastSentPacket :" + lastSentPacket );
     				System.out.println("- Resend Packet -");
     				//packetCounter--;
     				
     				// safeN is used to prevent infinite loop
     				
     				byte[] sendData = new byte[4096];                     
                    sendData = modifiedClientMessage.getBytes();                     
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport); 
                    System.out.println("- resend packet to Server ("+ serverIPAddress.toString()+":" +serverport +") :"+modifiedClientMessage+" -");
                    udpClientSocket.send(sendPacket);
   					// resend the message
                    
                    
     				safeCounter++;
                    Thread.sleep(300); // To yield, better way to do this?
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
        catch (InterruptedException e) {    // auto
			
			e.printStackTrace();  // auto
		}         //auto
    }
}   

 
class ReceiverThread extends Thread {
    
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
    static int transR (String commandR){
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
            
           
            
            // Set up a DatagramPacket to receive the data into
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
                int swR = transR (commandR); // Token[0] is command
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
              				 
              			 case 4: // <play> header
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
              			 case 41://<ackplay>
              				 String ackplayStatus = parseRToken[1].trim();
             				 System.out.println(serverReply);
             				if (ackplayStatus.equals("O")) {
             					 System.out.println("Occupied");
             				 } else if (ackplayStatus.equals("T")) {
             					 System.out.println("Out of turn");
             				 }
             				 
              				 
              				 break;
              			 case 31: //ackchoose
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
              			      
              			 case 8: //result
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