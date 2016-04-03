package Peer;

import java.io.File;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DeleteThread extends Thread {

    @Override
    public void run() {
        MulticastSocket controlSocket;
        DatagramPacket dataPacket;
        byte[] buf;
        String received;
        String[] msg;
        int i;
        ArrayList<String[]> localChunkInfo, remoteChunkInfo, filter;
        try {
            controlSocket = new MulticastSocket(Peer.getMCport());
            controlSocket.joinGroup(Peer.getMCip());
            controlSocket.setLoopbackMode(true);
            controlSocket.setSoTimeout(100);
            buf = new byte[256];
            dataPacket = new DatagramPacket(buf, buf.length);
            while (Peer.running) try {
                controlSocket.receive(dataPacket);
                received = new String(dataPacket.getData(), 0, dataPacket.getLength(), StandardCharsets.ISO_8859_1);
                i = received.indexOf("\r\n\r\n");
                received = new String(dataPacket.getData(), 0, i, StandardCharsets.ISO_8859_1);
                msg = received.split("[ ]+");
                if (msg[0].equals("DELETE")) {
                    System.out.println("DeleteThread  - Received from " + dataPacket.getAddress() + ":" +
                            dataPacket.getPort() + " | " + received);
                    localChunkInfo = FileHandle.loadLocalChunkInfo();
                    filter = new ArrayList<>();
                    for (String[] chunk : localChunkInfo) {
                        if (chunk[0].equals(msg[3]))
                            filter.add(chunk);
                    }
                    for (String[] chunk : filter) {
                        System.out.println("DeleteThread  - " + chunk[0] + ".part" + chunk[1] + " erased.");
                        new File(chunk[0] + ".part" + chunk[1]).delete();
                        localChunkInfo.remove(chunk);
                    }
                    FileHandle.saveLocalChunkInfo(localChunkInfo);
                } else if (msg[0].equals("REMOVED")) {
                    System.out.println("DeleteThread  - Received from " + dataPacket.getAddress() + ":" +
                            dataPacket.getPort() + " | " + received);
                    localChunkInfo = FileHandle.loadLocalChunkInfo();
                    remoteChunkInfo = FileHandle.loadRemoteChunkInfo();
                    for (String[] chunk : localChunkInfo) {
                        if (chunk[0].equals(msg[3]) && chunk[1].equals(msg[4])) {
                            chunk[4] = (Integer.parseInt(chunk[4]) - 1) + "";
                            FileHandle.saveLocalChunkInfo(localChunkInfo);
                            if (Integer.parseInt(chunk[4]) < Integer.parseInt(chunk[3])) {
                                BackupProtocol.run(chunk, true);
                            }
                            break;
                        }
                    }
                    for (String[] chunk : remoteChunkInfo) {
                        if (chunk[0].equals(msg[3]) && chunk[1].equals(msg[4])) {
                            chunk[4] = (Integer.parseInt(chunk[4]) - 1) + "";
                            FileHandle.saveRemoteChunkInfo(remoteChunkInfo);
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {
            }
            controlSocket.leaveGroup(Peer.getMCip());
            controlSocket.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}

