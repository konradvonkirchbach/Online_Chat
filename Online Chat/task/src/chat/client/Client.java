package chat.client;

import chat.client.utils.PropertyHolder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String ip = (String) PropertyHolder.properties.get("ip_address");
        int port = Integer.valueOf((String) PropertyHolder.properties.get("port"));
        OutputReader or = null;
        InputReader ir = null;
        try (Socket socket = new Socket(InetAddress.getByName(ip), port);
             DataInputStream is = new DataInputStream(socket.getInputStream());
             DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
            ir = new InputReader(is, socket);
            or = new OutputReader(socket, os);
            System.out.println("Client started!");
            ir.start();
            or.start();
            or.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class InputReader extends Thread {
    private DataInputStream is;

    private Socket socket;

    public InputReader(DataInputStream is,
                       Socket socket) {
        this.is = is;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                System.out.println(is.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof SocketException) {
                    break;
                }
            }
        }
    }
}

class OutputReader extends Thread {

    private Socket socket;
    private DataOutputStream os;

    private Scanner scanner = new Scanner(System.in);

    public OutputReader(Socket socket, DataOutputStream os) {
        this.socket = socket;
        this.os = os;
    }

    private void sendMessage(String message) {
        try {
            os.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            if (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                sendMessage(nextLine);
                if ("/exit".equals(nextLine)) {
                    return;
                }
            }
        }
    }
}