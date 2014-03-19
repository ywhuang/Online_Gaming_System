package edu.columbia.gamesys.client;

public class SharedData {
	   String myName = "unKnown";
	   boolean valueSet = false;
	   boolean packetReceived = false;
	   int currentPacketCounter = 0;  // i.e.Nonce 
	   String cachedMessage ="Not yet cached";
	   
	   //synchronized  
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
