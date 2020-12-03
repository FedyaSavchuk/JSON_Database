package server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    static ServerSocket server;

    public static void main(String[] args) throws Exception {
        startServer();
    }

    static void startServer() throws Exception {
        String address = "127.0.0.1";
        int port = 23456;
        server = new ServerSocket(port, 50, InetAddress.getByName(address));
        System.out.println("Server started!");

        while (!server.isClosed()) {
            Socket socket = server.accept();
            new Thread(() -> {
                try {
                    DatabaseController.requestHandler(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
