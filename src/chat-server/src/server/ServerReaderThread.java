package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class ServerReaderThread extends Thread{
    private Socket socket;
    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            // define 1 for login message; 2 for group chat message; 3 for private chat message
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();
                switch (type){
                    case 1:
                        // login message
                        String nickname = dis.readUTF();
                        Server.onlineClients.put(socket, nickname);
                        updateOnlineClientList();
                        break;
                    case 2:
                        // group chat message
                        String content = dis.readUTF();
                        sendMsgToAll(content);
                        break;
                    case 3:
                        // private chat message
                        break;
                }
            }
        }catch (Exception e){
            System.out.println("client offline: " + socket.getInetAddress().getHostAddress());
            Server.onlineClients.remove(socket);
            updateOnlineClientList();
        }
    }

    /**
     * send message to all online clients
     */
    private void sendMsgToAll(String content) {
        StringBuilder sb = new StringBuilder();
        String name = Server.onlineClients.get(socket);

        // get current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE a");
        String time = now.format(dtf);

        sb.append(name).append(" ").append(time).append("\r\n").append(content).append("\r\n");

        for (Socket socket : Server.onlineClients.keySet()) {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(2); // define 2 for group chat message
                dos.writeUTF(sb.toString());
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOnlineClientList() {
        // get all online users nickname
        Collection<String> onlineUsers = Server.onlineClients.values();

        for (Socket socket : Server.onlineClients.keySet()) {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(1);  // define 1 for online user list message
                dos.writeInt(Server.onlineClients.size());
                for (String nickname : onlineUsers) {
                    dos.writeUTF(nickname);
                }
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
