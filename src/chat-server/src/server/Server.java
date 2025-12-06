package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    // define a map to collect all online clients socket
    public static final Map<Socket, String> onlineClients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Server System Startup......");

        //1. register port
        try (
                ServerSocket serverSocket = new ServerSocket(Constant.PORT);
        ) {
            while (true) {
                System.out.println("waiting for client connect");

                //2. get client socket
                Socket socket = serverSocket.accept();
                new ServerReaderThread(socket).start();

                System.out.println("one client connected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
