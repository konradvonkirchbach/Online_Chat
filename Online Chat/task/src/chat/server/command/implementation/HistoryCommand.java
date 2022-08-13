package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.message.Message;
import chat.server.message.MessageService;
import chat.server.serverresponses.ErrorResponses;
import chat.server.user.User;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

import java.util.List;

public class HistoryCommand extends SocketHandler implements Command {

    private ClientHandler clientHandler;

    private MessageService messageService = MessageService.getInstance();

    public HistoryCommand(ClientHandler clientHandler) {
        this.socket = clientHandler.getSocket();
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        String currentMessage = clientHandler.getCurrentMessage();
        String numberToken = currentMessage.split("\\s+")[1];

        try {
            int size = Integer.parseInt(numberToken);
            User user = clientHandler.getAddressee().getUser();
            List<Message> history = messageService.getHistory(clientHandler.getUser(),
                    user,
                    size);
            writeMessage("Server:");
            history.forEach(m -> writeMessage(m.getSimpleForm()));
        } catch (NumberFormatException e) {
            writeMessage(String.format(ErrorResponses.NOT_A_NUMBER.getText(), numberToken));
            return;
        }
    }
}
