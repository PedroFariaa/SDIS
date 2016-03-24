import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Peer {

	protected static volatile boolean peerON = true;
	private static InetAddress MCip;
	private static int MCport;
	private static InetAddress MCBip;
	private static int MCBport;
	private static InetAddress MCRip;
	private static int MCRport;
	public static boolean active;

	public static void main(String[] args) throws IOException {
		MCip = InetAddress.getByName("225.0.0.1");
		MCport = 5001;
		MCBip = InetAddress.getByName("225.0.0.1");
		MCBport = 5002;
		MCRip = InetAddress.getByName("225.0.0.1");
		MCRport = 5003;

		// criar threads de protocolos
		new BackupThread().start();

	Scanner sc = new Scanner(System.in);

	while(Peer.peerON)
	{

		try {
			String input = sc.nextLine();
			String[] command = commandValidate(input);
			switch (Integer.parseInt(command[0])) {
			case -1:
				System.out.println("Invalid command or filename, type 'USAGE' for a list of options");
				break;
			case 0:
				Peer.peerON = false;
				System.exit(0);
				break;
			case 1:
				 BackupProtocol.run(command);
				break;
			case 2:
				// RestoreProtocol.run(command);
				break;
			case 3:
				// DeleteProtocol.run(command);
				break;
			case 4:
				// ReclaimProtocol.run();
				break;
			case 5:
				System.out.println("Valid operations:\n" + "\tBACKUP <filename> <replication factor>\n"
						+ "\tRESTORE <filename>\n" + "\tDELETE <filename>\n" + "\tRECLAIM\n" + "\tEXIT\n");
				break;
			default:
				return;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	sc.close();
	}
	

}

public static String[] commandValidate(String string) throws Exception {
	String[] tokens = string.split("[ ]+");
	if (tokens[0].equalsIgnoreCase("EXIT")) {
		tokens[0] = "0";
	} else if (tokens[0].equalsIgnoreCase("BACKUP") && tokens.length == 3) {
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

	public static InetAddress getMCip() {
		return MCip;
	}
	
	public static int getMCport() {
		return MCport;
	}

	public static InetAddress getMCBip() {
		return MCBip;
	}

	public static InetAddress getMCRip() {
		return MCRip;
	}

	public static int getMCBport() {
		return MCBport;
	}

}
