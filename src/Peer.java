import java.io.IOException;
import java.net.*;


public class Peer {

	private static InetAddress MCip;
    private static int MCport;
    private static InetAddress MCBip;
    private static int MCBport;
    private static InetAddress MCRip;
    private static int MCRport;
	public static boolean active;
    
    
	public static void main(String[] args) throws IOException {
		MCip = InetAddress.getByName("225.0.0.1");
        MCport = 9001;
        MCBip = InetAddress.getByName("225.0.0.1");
        MCBport = 9002;
        MCRip = InetAddress.getByName("225.0.0.1");
        MCRport = 9003;
        
        
        // criar threads de protocolos
    	BackupProtocol backupProtocol = new BackupProtocol();
        
        new BackupThread().start();
    	
    	BackupProtocol.run();
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
