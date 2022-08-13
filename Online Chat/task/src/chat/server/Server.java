package chat.server;

import chat.server.utils.ClientHandler;
import chat.server.utils.PropertyHolder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Server {

    public static void main(String[] args) {
        System.out.println("Server started!");
        String ip = PropertyHolder.properties.getProperty("ip_address");
        int port = Integer.parseInt(PropertyHolder.properties.getProperty("port"));

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip))) {
            Instant deadLine = Instant.now().plusSeconds(10);
            Boolean shouldRunForever = Boolean.getBoolean(PropertyHolder.getProperty("run_forever"));
            serverSocket.setSoTimeout(3_000);
            while (shouldRunForever || Instant.now().isBefore(deadLine)) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clientHandler.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
