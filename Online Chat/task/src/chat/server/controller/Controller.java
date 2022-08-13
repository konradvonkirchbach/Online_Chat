package chat.server.controller;

import chat.server.command.Command;

public class Controller {

    private Command command;

    public void executeCommand() {
        if (command != null) {
            command.execute();
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }

}
