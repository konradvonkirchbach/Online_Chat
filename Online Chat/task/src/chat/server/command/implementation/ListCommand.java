package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.dtos.ClientsOnline;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

public class ListCommand extends SocketHandler implements Command {

    private ClientHandler clientHandler;

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public ListCommand(ClientHandler clientHandler) {
        socket = clientHandler.getSocket();
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        if (clientsOnline.amITheOnlyOneOnline(clientHandler)) {
            writeMessage("Server: no one online");
        } else {
            writeMessage("Server: online: " + clientsOnline.getOtherOnlineClientsName(clientHandler));
        }
    }
}
