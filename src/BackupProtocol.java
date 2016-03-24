import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class BackupProtocol {

	public static void run(String args[]) {
		MulticastSocket multicastSocket, otherSocket;
		DatagramPacket chunkPacket, ackPacket;
		int chunkN, saved, attempt, timeout;
		File file;
		FileInputStream fis;
		ArrayList<String[]> chunkInfo, fileInfo;
		long t0, t1;
		String[] temp;
		byte[] msg;

		try {
			multicastSocket = new MulticastSocket();
			multicastSocket.joinGroup(Peer.getMCBip());
			multicastSocket.setLoopbackMode(true);
			multicastSocket.setSoTimeout(100);

			otherSocket = new MulticastSocket(Peer.getMCport());
			otherSocket.joinGroup(Peer.getMCip());
			otherSocket.setLoopbackMode(true);
			otherSocket.setSoTimeout(100);

			try {
				chunkInfo = FileHandle.loadRemoteChunkInfo();
				fileInfo = FileHandle.loadFileInfo();
			

			file = new File(args[1] + ".part");

			fis = new FileInputStream(file);
			String fileID = FileHandle.getFileID(args[1]);
			chunkN = 0;
			byte[] chunkBuf = new byte[64000];
			byte[] buf = new byte[100];
			ArrayList<String> IPlist = new ArrayList<>();
			int k;
			while ((k = fis.read(chunkBuf)) > -1) {
				msg = FileHandle.concatenateByteArrays(
						buildHeader(fileID, chunkN, args[2]).getBytes(StandardCharsets.ISO_8859_1),
						Arrays.copyOfRange(chunkBuf, 0, k));
				chunkPacket = new DatagramPacket(msg, msg.length, Peer.getMCBip(), Peer.getMCBport());
				ackPacket = new DatagramPacket(buf, buf.length);
				attempt = 1;
				saved = 0;
				IPlist.clear();
				do {
					timeout = (int) (500 * Math.pow(2, attempt - 1) / Integer.parseInt(args[2]));
					otherSocket.setSoTimeout(timeout);
					multicastSocket.send(chunkPacket);
					t0 = System.currentTimeMillis();
					do {
						try {
							otherSocket.receive(ackPacket);
							String z = new String(ackPacket.getData(), 0, ackPacket.getLength(),
									StandardCharsets.ISO_8859_1);
							int j = z.indexOf("\r\n\r\n");
							z = new String(ackPacket.getData(), 0, j, StandardCharsets.ISO_8859_1);
							System.out.println("BackupProtocol  - Received from " + ackPacket.getAddress() + ":"
									+ ackPacket.getPort() + " | " + z);
							if (validateAcknowledge(ackPacket, IPlist, fileID, chunkN)) {
								saved++;
							}
						} catch (SocketTimeoutException ignore) {
						}
						t1 = System.currentTimeMillis();
					} while ((t1 - t0) < (500 * Math.pow(2, attempt - 1)));
					attempt++;
				} while (saved < Integer.parseInt(args[2]) && attempt <= 5);
				temp = new String[5];
				temp[0] = fileID;
				temp[1] = chunkN + "";
				temp[2] = k + "";
				temp[3] = args[2];
				temp[4] = saved + "";
				
				chunkInfo.add(temp);
				chunkN++;
			}
			temp = new String[2];
			temp[0] = file.getName();
			temp[1] = fileID;

			fileInfo.add(temp);
			multicastSocket.leaveGroup(Peer.getMCBip());
			multicastSocket.close();
			otherSocket.leaveGroup(Peer.getMCip());
			otherSocket.close();
			fis.close();

			FileHandle.saveRemoteChunkInfo(chunkInfo);
			FileHandle.saveFileInfo(fileInfo);

			System.out.println("BackupProtocol  - Finished");
			
			} catch (Exception ignore) {

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	static String buildHeader(String fileID, int chunkN, String factor) {
        return "PUTCHUNK 1.0 " + fileID + " " + chunkN + " " + factor + " \r\n\r\n";
    }

    static boolean validateAcknowledge(DatagramPacket ack, ArrayList<String> ip, String fileID, int chunk) {
        boolean exists = false;
        String s = new String(ack.getData(), 0, ack.getLength(), StandardCharsets.ISO_8859_1);
        String[] msg = s.split("[ ]+");

        if (msg[0].trim().equals("STORED") && msg[2].trim().equals(fileID) && Integer.parseInt(msg[3]) == chunk) {
            for (String aIp : ip) {
                if (aIp.equals(ack.getAddress().toString() + ":" + ack.getPort())) {
                    exists = true;
                }
            }
            if (!exists) {
                ip.add(ack.getAddress().toString() + ":" + ack.getPort());
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
