package chat.server.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public abstract class SocketHandler {

    protected Socket socket;

    protected Optional<String> readMessage() {
        if (socket == null) {
            System.err.println("Socket not set");
            return Optional.empty();
        }

        try {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            return Optional.of(is.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    protected void writeMessage(String message) {
        if (socket == null) {
            System.err.println("Socket not set");
            return;
        }

        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            os.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
