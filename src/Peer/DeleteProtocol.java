package Peer;

import java.io.File;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DeleteProtocol {
    public static void run(String[] args) {
        MulticastSocket multicastSocket;
        DatagramPacket controlPacket;
        File file;
        String[] fileFilter;
        int sent;
        byte[] buf;
        ArrayList<String[]> fileInfo, chunkInfo, filter;

        try {
            chunkInfo = Util.loadRemoteChunkInfo();
            fileInfo = Util.loadFileInfo();
            file = new File(args[1]);
            if (!Util.fileExists(fileInfo, file)) {
                System.out.println("DeleteProtocol  - File was not backed up");
                return;
            }
            multicastSocket = new MulticastSocket();
            multicastSocket.joinGroup(Peer.getMCip());
            multicastSocket.setLoopbackMode(true);
            multicastSocket.setSoTimeout(100);
            fileFilter = Util.filterFiles(fileInfo, file.getName());
            filter = Util.filterChunks(chunkInfo, fileFilter[1]);
            buf = buildHeader(fileFilter).getBytes(StandardCharsets.ISO_8859_1);
            controlPacket = new DatagramPacket(buf, buf.length, Peer.getMCip(), Peer.getMCport());
            sent = 0;
            while (sent < 5) {
                multicastSocket.send(controlPacket);
                sent++;
                Util.wait(1000);
            }
            for (String[] chunk : filter) {
                chunkInfo.remove(chunk);
            }
            fileInfo.remove(fileFilter);
            Util.saveFileInfo(fileInfo);
            Util.saveRemoteChunkInfo(chunkInfo);
            multicastSocket.close();
            System.out.println("DeleteProtocol  - Finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String buildHeader(String[] cmd) {
        return "DELETE 1.0 " + Peer.senderID + " " + cmd[1] + " \r\n\r\n";
    }
}
