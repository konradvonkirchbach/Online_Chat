package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.command.Commands;
import chat.server.dtos.ClientsOnline;
import chat.server.serverresponses.ErrorResponses;
import chat.server.user.User;
import chat.server.user.UserService;
import chat.server.utils.ClientHandler;
import chat.server.utils.HashingUtils;
import chat.server.utils.SocketHandler;

import java.util.Optional;
import java.util.logging.Logger;

public class RegistrationAuthorizationCommand extends SocketHandler implements Command {

    private final Logger logger = Logger.getLogger(RegistrationAuthorizationCommand.class.getName());

    private UserService userService;

    private Commands command;

    private String username;

    private String password;

    private boolean processComplete = false;

    private boolean isKicked;

    private User user;

    private ClientHandler clientHandler;

    private ClientsOnline clientsOnline = ClientsOnline.getInstance();

    public RegistrationAuthorizationCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userService = UserService.getInstance();
        this.socket = clientHandler.getSocket();
    }

    @Override
    public void execute() {
        while (!processComplete && !isKicked) {
            readAndAssignUser();
            switch (command) {
                case REGISTRATION: {
                    registration();
                    break;
                }
                case AUTHENTICATION: {
                    authenticate();
                    break;
                }
            }
        }
        if (processComplete) {
            userService.addUser(user);
            clientHandler.setUser(user);
            clientsOnline.addClient(clientHandler);
        }
    }

    public boolean isProcessComplete() {
        return processComplete;
    }

    private void registration() {
        boolean isValid = !usernameExists(username) && passwordIsValid(password);
        while (!isValid) {
            if (usernameExists(username)) {
                writeMessage(ErrorResponses.LOGIN_TAKEN.getText());
                readAndAssignUser();
                continue;
            }

            if (!passwordIsValid(password)) {
                writeMessage(ErrorResponses.PASSWORD_TOO_SHORT.getText());
                readAndAssignUser();
            }

            if (command != Commands.REGISTRATION) {
                return;
            }

            isValid = !usernameExists(username) && passwordIsValid(password);
        }
        user = new User(username, HashingUtils.hash(password));
        writeMessage("Server: you are registered successfully!");
        processComplete = true;
    }

    private boolean passwordIsValid(String password) {
        return password.length() > 7;
    }

    private boolean usernameExists(String username) {
        return username != null && userService.usernameExists(username);
    }

    private void authenticate() {
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            user = userService.findByName(username);

            if (!usernameExists(username)) {
                writeMessage(ErrorResponses.INCORRECT_LOGIN.getText());
                readAndAssignUser();
                continue;
            }

            if (!userService.authenticateUser(new User(username, HashingUtils.hash(password)))) {
                writeMessage(ErrorResponses.INCORRECT_PASSWORD.getText());
                readAndAssignUser();
                continue;
            }

            if (user.isKicked()) {
                writeMessage(ErrorResponses.IS_BANNED.getText());
                isKicked = true;
                return;
            }

            if (command != Commands.AUTHENTICATION) {
                return;
            }

            isAuthenticated = userService.authenticateUser(user);
        }
        writeMessage("Server: you are authorized successfully!");
        processComplete = true;
    }

    private void readAndAssignUser() {
        while (true) {
            Optional<String> optionalMessage = Optional.ofNullable(clientHandler.getCurrentMessage());
                if (optionalMessage.isEmpty()) {
                    optionalMessage = readMessage();
                }
                String message = optionalMessage.get();
                String[] tokens = message.split("\\s");
                if (!Commands.isCommand(message)) {
                    writeMessage(ErrorResponses.NOT_IN_CHAT.getText());
                } else {
                    command = Commands.fromText(tokens[0]);
                }
                if (!Commands.getAuthenticationRegistrationCommands().contains(command)) {
                    if (command == Commands.CHAT) {
                        writeMessage(ErrorResponses.NOT_IN_CHAT.getText());
                    } else {
                        writeMessage(ErrorResponses.INCORRECT_COMMAND.getText());
                    }
                } else {
                    if (tokens.length > 2) {
                        username = tokens[1];
                        password = tokens[2];
                        break;
                    }
                }
        }
    }
}
