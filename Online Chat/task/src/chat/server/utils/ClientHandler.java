package chat.server.utils;

import chat.server.command.Command;
import chat.server.command.Commands;
import chat.server.command.implementation.*;
import chat.server.controller.Controller;
import chat.server.dtos.ClientsOnline;
import chat.server.serverresponses.ErrorResponses;
import chat.server.message.Message;
import chat.server.message.MessageService;
import chat.server.user.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ClientHandler extends Thread {

    private Logger logger = Logger.getLogger(ClientHandler.currentThread().getName());

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    private User user;

    private MessageService messageService;

    private Controller controller = new Controller();

    private ClientHandler addressee;

    private String currentMessage;

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.messageService = MessageService.getInstance();
        try {
            this.is = new DataInputStream(socket.getInputStream());
            this.os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            closeSocket();
        }
    }

    @Override
    public void run()  {
        if (socket == null) {
            return;
        }

        // registration or authentication
        writeMessage("Server: authorize or register");
        RegistrationAuthorizationCommand rac = new RegistrationAuthorizationCommand(this);
        controller.setCommand(rac);
        controller.executeCommand();
        if (!rac.isProcessComplete()) {
            closeSocket();
        }

        // continuous chatting to group or people
        while (socket != null && socket.isConnected()) {
            Optional<String> optionalMessage = readMessage();
            if (optionalMessage.isPresent()) {
                currentMessage = optionalMessage.get();
                // perform the command
                if (Commands.isCommand(currentMessage)) {
                    Command command = buildCommand(currentMessage);
                    controller.setCommand(command);
                    controller.executeCommand();
                } else if (!clientsOnline.isUserOnline(this)) {
                    writeMessage(ErrorResponses.NOT_IN_CHAT.getText());
                }// client did not choose an addresse
                else if (addressee == null) {
                    writeMessage(ErrorResponses.NO_ADDRESSE.getText());
                } // send to addresse
                else {
                    boolean isOnline = clientsOnline.isUserOnline(addressee);
                    Message message = new Message(currentMessage,
                            isOnline,
                            user,
                            addressee.user);

                    writeMessage(message.getSimpleForm());

                    if (isOnline && addressee.getAddressee() == this) {
                        message.setNew(false);
                        addressee.writeMessage(message.toString());
                    }

                    messageService.addMessage(message);
                }
            }
        }
        closeSocket();
        logger.info(user.getName() + " signed off");
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return user.getName();
    }

    public final Socket getSocket() {
        return socket;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setAddressee(ClientHandler addressee) {
        this.addressee = addressee;
    }

    public ClientHandler getAddressee() {
        return addressee;
    }

    public void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket = null;
        clientsOnline.removeUser(this);
    }

    public void writeMessage(String message) {
        synchronized (this) {
            try {
                os.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Optional<String> readMessage() {
        try {
            return Optional.of(is.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Command buildCommand(String message) {
        Commands command = Commands.fromText(message.split("\\s+")[0]);
        switch (command) {
            case REGISTRATION:
            case AUTHENTICATION: return new RegistrationAuthorizationCommand(this);
            case LIST: return new ListCommand(this);
            case CHAT: return new ChatCommand(this);
            case GRANT: return new GrantCommand(this);
            case REVOKE: return new RevokeCommand(this);
            case KICK: return new KickCommand(this);
            case UNREAD: return new UnreadCommand(this);
            case STATS: return new StatsCommand(this);
            case HISTORY: return new HistoryCommand(this);
            case EXIT: return new ExitCommand(this);
            default: throw new IllegalArgumentException("No command found for " + command.getText());
        }
    }
}
