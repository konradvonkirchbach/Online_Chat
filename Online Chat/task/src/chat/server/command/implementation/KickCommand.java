package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.dtos.ClientsOnline;
import chat.server.serverresponses.ErrorResponses;
import chat.server.serverresponses.SuccessResponse;
import chat.server.user.User;
import chat.server.user.UserService;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

import java.util.Objects;
import java.util.logging.Logger;

public class KickCommand extends SocketHandler implements Command {

    private static Logger logger = Logger.getLogger(KickCommand.class.getName());

    private ClientHandler clientHandler;

    private UserService userService = UserService.getInstance();

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public KickCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        socket = clientHandler.getSocket();
    }

    @Override
    public void execute() {
        String name = clientHandler.getCurrentMessage().split("\\s+")[1];

        User user = userService.findByName(name);

        if (user != null) {
            if (name.equals(clientHandler.getUser().getName())) {
                writeMessage(ErrorResponses.KICK_ONESELF.getText());
            } else if (!clientHandler.getUser().isModerator() && !clientHandler.getUser().isAdmin()) {
                writeMessage(ErrorResponses.NOT_MODERATOR_ADMIN.getText());
            } else {
                user.kick();

                clientHandler.writeMessage(String.format(SuccessResponse.KICK_SUCCESS.getText(), name));

                ClientHandler toBeKicked = clientsOnline.findByUser(user);

                if (toBeKicked != null) {
                    toBeKicked.writeMessage(SuccessResponse.KICKED.getText());
                    clientsOnline.removeUser(toBeKicked);
                }

                userService.flush();
            }
        } else {
            logger.warning("No user with name " + name);
        }
    }
}
