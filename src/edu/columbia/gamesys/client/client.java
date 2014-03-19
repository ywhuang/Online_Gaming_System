package edu.columbia.gamesys.client;
import java.io.*;  
import java.net.*;
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
	

    public static void main(String args[]) throws Exception {  
 
        // The default port     
        int clientport = 7788;
        String host = "localhost"; // server address
        String serverReceiverHost = "localhost"; //server address for receiver
        InetAddress receiverServerIp = InetAddress.getByName( serverReceiverHost);
        int targetServerport = 4119; // default severport
        int receiverServerport = 4119;
        
        // Common shared data to be accessed by sender and receiver threads
        SharedData sharedData = new SharedData(); 
        
        if (args.length < 1) {
           System.out.println("Message: Client " + "now using host = " + host + ", Port# = " + clientport);
        } 
        // Get the port & the host number from user input
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
 
        // Get the IP address of the local machine - we will use this as the address to send the data.
        InetAddress ia = InetAddress.getByName(host);
        
        // Start both threads together ( with one Sharedata instance)
        
        SenderThread sender = new SenderThread(ia, clientport, targetServerport,sharedData);
        sender.start();
        System.out.println("Message: Client Receiver " + "now listens to IP = " + host + ", Port# = " + clientport);
        ReceiverThread receiver = new ReceiverThread(sender.getSocket(),sharedData,
        		                           receiverServerIp, receiverServerport);
        receiver.start();
    }
}

    //moved SharedData class  to a new java file  
	//moved SenderThread extends Thread {} to a separate java file
	//moved ReceiverThread extends Thread {} to a separate java file