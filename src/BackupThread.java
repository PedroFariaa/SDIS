import java.io.IOException;
import java.net.*;

public class BackupThread extends Thread{
	
	@Override
	public void run(){
		MulticastSocket backupSocket, multicastSocket;
				
		try {
			multicastSocket = new MulticastSocket();
			multicastSocket.joinGroup(Peer.getMCip());
	        multicastSocket.setLoopbackMode(true);
	        multicastSocket.setSoTimeout(100);
	        backupSocket = new MulticastSocket(Peer.getMCBport());
	        backupSocket.joinGroup(Peer.getMCBip());
	        backupSocket.setLoopbackMode(true);
	        backupSocket.setSoTimeout(100);
	        
	        
	        while(Peer.active){
	        	try{
	        		
	        		
	        	} catch(Exception ignore){
	        		
	        	}
	        	backupSocket.leaveGroup(Peer.getMCip());
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}
}
