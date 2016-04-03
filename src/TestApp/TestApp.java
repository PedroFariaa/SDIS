package TestApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import Peer.FileHandle;


public class TestApp {

	public static void main(String[] args) {

		DatagramSocket interfaceSocket;
		DatagramPacket cmdPacket;
		
		byte[] msg;


		try {
			interfaceSocket = new DatagramSocket();
			
			String input = new String("");
			int k=0;
			for(; k < args.length-1; k++)
				input+= args[k]+ " ";
			input+=args[k];
			
				try {
					String[] command = validateCmd(input);
					
					 switch (Integer.parseInt(command[0])) {
	                    case -1:
	                        System.out.println("Invalid command or filename, type 'USAGE' for a list of options");
	                        break;
	                    case 5:
	                        System.out.println("Valid operations:\n" +
	                                "\tBACKUP <filename> <replication factor>\n" +
	                                "\tRESTORE <filename>\n" +
	                                "\tDELETE <filename>\n" +
	                                "\tRECLAIM\n" +
	                                "\tEXIT\n");
	                        break;
	                    default:
	                    	msg =  command[0].getBytes(StandardCharsets.ISO_8859_1);
	                    	for(int i = 1 ; i < command.length ; i++){
	                    		msg = FileHandle.concatenateByteArrays(msg, new String(" ").getBytes(StandardCharsets.ISO_8859_1));
	                    		msg = FileHandle.concatenateByteArrays(msg, command[i].getBytes(StandardCharsets.ISO_8859_1));                   		
	                    	}
	                    	cmdPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName("localhost"), 9999);
	                    	
	                    	String cmd = new String(cmdPacket.getData(), 0, cmdPacket.getLength(), StandardCharsets.ISO_8859_1);
	        				System.out.println(cmd);
	                    	interfaceSocket.send(cmdPacket);
	          
	                        
	                }
					
					
				} catch (Exception e) {
					//e.printStackTrace();
				}
			//sc.close();
		}
		catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();

	}

}

public static String[] validateCmd(String s) throws Exception {
	String[] tokens = s.split("[ ]+");
	if (tokens[0].equals("exit")) {
		tokens[0] = "0";
	} else if (tokens[0].equalsIgnoreCase("BACKUP") && tokens.length == 3 && FileHandle.fileIsValid(tokens[1])
			&& Integer.parseInt(tokens[2]) > 0 && Integer.parseInt(tokens[2]) < 10) {
		tokens[0] = "1";
	} else if (tokens[0].equalsIgnoreCase("RESTORE") && tokens.length == 2) {
		tokens[0] = "2";
	} else if (tokens[0].equalsIgnoreCase("DELETE") && tokens.length == 2) {
		tokens[0] = "3";
	} else if (tokens[0].equalsIgnoreCase("RECLAIM") && tokens.length == 1) {
		tokens[0] = "4";
	} else if (tokens[0].equalsIgnoreCase("USAGE") && tokens.length == 1) {
		tokens[0] = "5";
	} else {
		tokens[0] = "-1";
	}
	return tokens;
}

}
