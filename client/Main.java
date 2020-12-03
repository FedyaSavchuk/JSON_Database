package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    static Args args = new Args();


    public static void main(String[] argv) throws Exception {
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);

        connectToServer("127.0.0.1", 23456);
    }

    static void connectToServer(String address, int port) throws Exception {
        Socket socket = new Socket(InetAddress.getByName(address), port);
        System.out.println("Client started!");

        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        Gson gson = new Gson();
        StringBuilder request = new StringBuilder();
        if (args.getFile() != null) {
            InputStream is = new FileInputStream("/Users/fedyasavchuk/projects/JSON Database/JSON Database/task/src/client/data/" + args.getFile());
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            while (buf.ready()) {
                request.append(buf.readLine());
            }

        } else {
            request.append(gson.toJson(args));
        }

        System.out.println("Sent: " + request);

        output.writeUTF(request.toString());

        String response = input.readUTF();
        System.out.println("Received: " + response);

        output.close();
        input.close();
        socket.close();
    }
}