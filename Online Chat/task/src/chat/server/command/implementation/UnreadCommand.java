package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.message.MessageService;
import chat.server.serverresponses.SuccessResponse;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

public class UnreadCommand extends SocketHandler implements Command {

    private ClientHandler clientHandler;

    private MessageService messageService = MessageService.getInstance();

    public UnreadCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        String users = messageService.getUserWithUnreadMessages(clientHandler.getUser());
        if (users == null || users.isBlank()) {
            clientHandler.writeMessage(SuccessResponse.NO_UNREAD_MESSAES.getText());
        } else {
            clientHandler.writeMessage("Server: unread from: " + users);
        }
    }
}
