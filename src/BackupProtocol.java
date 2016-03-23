import java.io.*;
import java.net.*;

public class BackupProtocol {
	
	public static void run(){
		MulticastSocket multicastSocket, otherSocket;
		try {
			multicastSocket = new MulticastSocket();
			multicastSocket.joinGroup(Peer.getMCBip());
            multicastSocket.setLoopbackMode(true);
            multicastSocket.setSoTimeout(100);
            
            otherSocket = new MulticastSocket(Peer.getMCport());
            otherSocket.joinGroup(Peer.getMCip());
            otherSocket.setLoopbackMode(true);
            otherSocket.setSoTimeout(100);
            		
            //FileInputStream fileis;
            byte[] buf = new byte[64100];
            DatagramPacket chunkPacket = new DatagramPacket(buf, buf.length, Peer.getMCBip(), Peer.getMCBport());
            DatagramPacket ackPacket = new DatagramPacket(buf, buf.length);
            multicastSocket.send(chunkPacket);
            
            System.out.println("asd");
            	try{
            		otherSocket.receive(ackPacket);
            		System.out.print("recieved");
		 } catch (SocketTimeoutException ignore) {
		
         }
            
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
