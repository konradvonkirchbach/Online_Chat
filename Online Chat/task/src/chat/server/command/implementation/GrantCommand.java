package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.dtos.ClientsOnline;
import chat.server.serverresponses.ErrorResponses;
import chat.server.serverresponses.SuccessResponse;
import chat.server.user.Roles;
import chat.server.user.User;
import chat.server.user.UserService;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

import java.util.logging.Logger;

public class GrantCommand extends SocketHandler implements Command {

    private static Logger logger = Logger.getLogger(GrantCommand.class.getName());

    private ClientHandler clientHandler;

    private UserService userService;

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public GrantCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.socket = clientHandler.getSocket();
        userService = UserService.getInstance();
    }

    @Override
    public void execute() {
        if (!clientHandler.getUser().isAdmin()) {
            writeMessage(ErrorResponses.NOT_ADMINISTRATOR.getText());
        }

        String message = clientHandler.getCurrentMessage();
        String name = message.split("\\s+")[1];

        User user = userService.findByName(name);

        if (user != null) {
            if (user.getRole() == Roles.ADMIN) {
                logger.warning("Admin was about to get denoted");
            }
            if (user.getRole() == Roles.MODERATOR) {
                logger.warning(String.format("User %s is already a moderator", name));
                writeMessage(ErrorResponses.ALREADY_MODERATOR.getText());
            } else {
                user.setRole(Roles.MODERATOR);

                writeMessage(String.format(SuccessResponse.GRANT_SUCCESS.getText(), user.getName()));

                // notify the user
                ClientHandler toBeGranted = clientsOnline.findByUser(user);
                if (toBeGranted != null) {
                    toBeGranted.writeMessage(SuccessResponse.PROMOTED_TO_MODERATOR.getText());
                }

                userService.flush();
            }
        } else {
            logger.warning("No user found with name " + name);
        }
    }
}
