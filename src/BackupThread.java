import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class BackupThread extends Thread{
	
	@Override
	public void run(){
		MulticastSocket backupSocket, multicastSocket;
		String received = "";
				
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
	        		byte[] buf = new byte[64100];
	        		DatagramPacket msg = new DatagramPacket(buf, buf.length);
	        		backupSocket.receive(msg);
	        		received = new String(msg.getData(), 0, msg.getLength(), StandardCharsets.ISO_8859_1);
	        		if (received != null){
	        			System.out.println("YEYYYYYY !!!!!!");
	        		}else {
	        			System.out.println("FUNCIONA FDP !!!!!!!!!");
	        		}
	        		
	        		byte[] peerAck = new byte[256];
                    DatagramPacket peerAckPacket = new DatagramPacket(peerAck, peerAck.length);
                    
                        try {
                            multicastSocket.receive(peerAckPacket);
                            
                        } catch (Exception ignore) {
                        }
	        		
	        		
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
