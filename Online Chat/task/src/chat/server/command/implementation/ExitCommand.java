package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.dtos.ClientsOnline;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

public class ExitCommand extends SocketHandler implements Command {

    private ClientHandler clientHandler;

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public ExitCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        clientsOnline.removeUser(clientHandler);
        clientHandler.closeSocket();
    }
}
