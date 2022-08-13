package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.dtos.ClientsOnline;
import chat.server.serverresponses.ErrorResponses;
import chat.server.message.MessageService;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatCommand extends SocketHandler implements Command {

    private ClientHandler clientHandler;

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public ChatCommand(ClientHandler clientHandler) {
        socket = clientHandler.getSocket();
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        String currentMessage = clientHandler.getCurrentMessage();
        String username = Arrays.stream(currentMessage.split("\\s"))
                .skip(1)
                .collect(Collectors.joining(" "));

        Optional<ClientHandler> potentialClient = clientsOnline.findByUser(username);

        if (potentialClient.isPresent()) {
            clientHandler.setAddressee(potentialClient.get());
            MessageService.getInstance()
                    .getLastMessages(potentialClient.get().getUser(), clientHandler.getUser())
                    .forEach(message -> {
                        clientHandler.writeMessage(message.toString());
                        message.setNew(false);
                    });
        } else {
            writeMessage(ErrorResponses.USER_NOT_ONLINE.getText());
        }
    }
}
