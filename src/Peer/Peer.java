package Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Peer {
	protected static volatile boolean running = true;
	private static InetAddress MCip;
	private static int MCport;
	private static InetAddress MCBip;
	private static int MCBport;
	private static InetAddress MCRip;
	private static int MCRport;
	public static int senderID;
	

	public static InetAddress getMCip() {
		return MCip;
	}

	public static int getMCport() {
		return MCport;
	}

	public static InetAddress getMCBip() {
		return MCBip;
	}

	public static int getMCBport() {
		return MCBport;
	}

	public static int getMCRport() {
		return MCRport;
	}

	public static InetAddress getMCRip() {
		return MCRip;
	}

	public static void main(String[] args) throws IOException {

		Random r = new Random();
	    senderID = r.nextInt(999999) + 1;
	    
		if (args.length == 0) {
			MCip = InetAddress.getByName("225.0.0.1");
			MCport = 9001;
			MCBip = InetAddress.getByName("225.0.0.1");
			MCBport = 9002;
			MCRip = InetAddress.getByName("225.0.0.1");
			MCRport = 9003;
		} else if (args.length == 6) {
			MCip = InetAddress.getByName(args[0]);
			MCport = Integer.parseInt(args[1]);
			MCBip = InetAddress.getByName(args[2]);
			MCBport = Integer.parseInt(args[3]);
			MCRip = InetAddress.getByName(args[4]);
			MCRport = Integer.parseInt(args[5]);
		} else {
			System.out
			.println("Wrong number of arguments. Expected \"Server <MC ip> <MC port> <MCB ip> <MCB port> <MCR ip> <MCR port>\"");
			return;
		}

		new BackupThread().start();
		new RestoreThread().start();
		new DeleteThread().start();


		DatagramSocket interfaceSocket;
		DatagramPacket cmdPacket;
		byte[] buf;
		String cmd;

		try {
			interfaceSocket = new DatagramSocket(9999);
			buf = new byte[1000];
			cmdPacket = new DatagramPacket(buf, buf.length,InetAddress.getByName("localhost"),9999);
			while (Peer.running) try {
				interfaceSocket.receive(cmdPacket);
				

				cmd = new String(cmdPacket.getData(), 0, cmdPacket.getLength(), StandardCharsets.ISO_8859_1);
				System.out.println("Peer  - Received cmd: " + cmd);

				parseCMD(cmd);



			}catch (Exception e) {
				System.out.println("ERROR - " + e.getMessage());
			}
		}catch (Exception e){

		}
	}

	public static void parseCMD(String cmd){
		try {
			String[] cmdToken = convertToToken(cmd);
			switch (Integer.parseInt(cmdToken[0])) {
			case 0:
				Peer.running= false;
				break;
			case 1:
				BackupProtocol.run(cmdToken, false);
				break;
			case 2:
				RestoreProtocol.run(cmdToken);
				break;
			case 3:
				DeleteProtocol.run(cmdToken);
				break;
			case 4:
				//ReclaimProtocol.run();
				break;
			default:
				return;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}


	public static String[] convertToToken(String s) throws Exception {
		String[] tokens = s.split("[ ]+");

		return tokens;
	}

}

