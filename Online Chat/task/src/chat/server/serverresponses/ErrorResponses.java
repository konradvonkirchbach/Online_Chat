package chat.server.serverresponses;

public enum ErrorResponses {
    INCORRECT_COMMAND("Server: incorrect command!"),
    LOGIN_TAKEN("Server: this login is already taken! Choose another one."),
    PASSWORD_TOO_SHORT("Server: the password is too short!"),
    INCORRECT_LOGIN("Server: incorrect login!"),
    INCORRECT_PASSWORD("Server: incorrect password!"),
    NO_ADDRESSE("Server: use /list command to choose a user to text!"),
    NOT_IN_CHAT("Server: you are not in the chat!"),
    USER_NOT_ONLINE("Server: the user is not online!"),

    // GRANT
    NOT_ADMINISTRATOR("Server: you are not an admin!"),

    ALREADY_MODERATOR("Server: this user is already a moderator!"),

    // REVOKE

    NOT_MODERATOR("Server: this user is not a moderator!"),

    // KICK
    KICK_ONESELF("Server: you can't kick yourself!"),
    NOT_MODERATOR_ADMIN("Server: you are not a moderator or an admin!"),
    IS_BANNED("Server: you are banned!"),

    // HISTORY
    NOT_A_NUMBER("Server: %s is not a number!");

    private String text;

    ErrorResponses(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
