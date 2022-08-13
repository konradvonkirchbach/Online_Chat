package chat.server.serverresponses;

public enum SuccessResponse {
    GRANT_SUCCESS("Server: %s is the new moderator!"),
    REVOKE_SUCCESS("Server: %s is no longer a moderator!"),
    KICK_SUCCESS("Server: %s was kicked!"),
    KICKED("Server: you have been kicked out of the server!"),
    NO_LONGER_MODERATOR("Server: you are no longer a moderator!"),
    PROMOTED_TO_MODERATOR("Server: you are the new moderator now!"),
    NO_UNREAD_MESSAES("Server: no one unread"),
    STATS("Server:\nStatistics with %s:\nTotal messages: %d\nMessages from %s: %d\nMessages from %s: %d");

    private String text;

    SuccessResponse(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
