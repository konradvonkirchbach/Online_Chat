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

public class RevokeCommand extends SocketHandler implements Command {

    private static Logger logger= Logger.getLogger(RevokeCommand.class.getName());

    private ClientHandler clientHandler;

    private UserService userService = UserService.getInstance();

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public RevokeCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.socket = clientHandler.getSocket();
    }

    @Override
    public void execute() {
        String name = clientHandler.getCurrentMessage().split("\\s+")[1];

        User user = userService.findByName(name);

        if (user != null) {

            if (!clientHandler.getUser().isAdmin()) {
                logger.info("Client is not an admin");
                writeMessage(ErrorResponses.NOT_ADMINISTRATOR.getText());
            } else if (!user.isModerator()) {
                logger.info("User to be revoked is not a moderator");
                writeMessage(ErrorResponses.NOT_MODERATOR.getText());
            } else {
                user.setRole(Roles.USER);

                writeMessage(String.format(SuccessResponse.REVOKE_SUCCESS.getText(), user.getName()));

                // notify the user
                ClientHandler toBeRevoked = clientsOnline.findByUser(user);
                toBeRevoked.writeMessage(SuccessResponse.NO_LONGER_MODERATOR.getText());

                userService.flush();
            }
        } else {
            logger.warning("No user found with name " + name);
        }
    }
}
