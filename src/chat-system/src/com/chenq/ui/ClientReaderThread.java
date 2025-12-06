package com.chenq.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientReaderThread extends Thread{
    private Socket socket;
    private DataInputStream dis;
    private ClientChatFrame win;

    public ClientReaderThread(Socket socket, ClientChatFrame clientChatFrame) {
        this.socket = socket;
        this.win = clientChatFrame;
    }
    @Override
    public void run() {
        try {
            // define 1 for login message; 2 for group chat message; 3 for private chat message
            dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();
                switch (type){
                    case 1:
                        // online user list update message from server
                        updateOnlineClientList();
                        break;
                    case 2:
                        // group chat message from server
                        getMsgToWin();
                        break;
                    case 3:
                        // private chat message
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateOnlineClientList() throws Exception {
        int count = dis.readInt();

        String[] onlineClientList = new String[count];
        for (int i = 0; i < count; i++) {
            String nickname = dis.readUTF();
            onlineClientList[i] = nickname;
        }

        win.updateOnlineUsers(onlineClientList);
    }

    private void getMsgToWin() throws Exception {
        String msg = dis.readUTF();
        win.setMsgToWin(msg);
    }
}
