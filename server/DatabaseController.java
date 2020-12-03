package server;

import com.google.gson.Gson;
import server.model.Request;
import server.model.Response;
import server.service.DatabaseService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseController {
    static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static void requestHandler(Socket socket) throws Exception {
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output  = new DataOutputStream(socket.getOutputStream());

        Gson gson = new Gson();
        Request request = gson.fromJson(input.readUTF(), Request.class);
        String response = gson.toJson(executeCommand(request));
        output.writeUTF(response);

        output.close();
        input.close();

        if (request.getType().equals("exit")) {
            socket.close();
            Main.server.close();
        }
    }

    public static Response executeCommand(Request request) {
        Response response;
        switch (request.getType()) {
            case "exit":
                return new Response("OK");
            case "get":
                lock.readLock().lock();
                response = DatabaseService.get(request.getKey());
                lock.readLock().unlock();
                return response;
            case "set":
                lock.writeLock().lock();
                response = DatabaseService.set(request.getKey(), request.getValue());
                lock.writeLock().unlock();
                return response;
            case "delete":
                lock.writeLock().lock();
                response = DatabaseService.delete(request.getKey());
                lock.writeLock().unlock();
                return response;
            default:
                return DatabaseService.error();
        }
    }
}
