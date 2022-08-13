package chat.server.command;

import java.util.EnumSet;
import java.util.Set;

public enum Commands {
    REGISTRATION("/registration"),
    AUTHENTICATION("/auth"),
    LIST("/list"),
    CHAT("/chat"),
    GRANT("/grant"),
    REVOKE("/revoke"),
    KICK("/kick"),
    UNREAD("/unread"),
    STATS("/stats"),
    HISTORY("/history"),
    EXIT("/exit");

    private String text;

    Commands(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Commands fromText(String text) {
        for (Commands c : Commands.values()) {
            if (c.getText().equals(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("No command found for " + text);
    }

    public static boolean isCommand(String token) {
        if (token == null) {
            return false;
        }
        for (Commands command : Commands.values()) {
            if (token.startsWith(command.getText())) {
                return true;
            }
        }
        return false;
    }

    public static Set<Commands> getAuthenticationRegistrationCommands() {
        return EnumSet.of(REGISTRATION, AUTHENTICATION);
    }
}
