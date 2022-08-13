package chat.server.message;

import chat.server.user.User;

import java.io.Serializable;

public class Message implements Serializable {

    private String message;

    private boolean isNew;

    private User sender;

    private User receiver;

    public Message(String message, boolean isNew, User sender, User receiver) {
        this.message = message;
        this.isNew = isNew;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public boolean isMessageOfChat(User sender, User receiver) {
        if (getReceiver() == null && getSender() == null) {
            return sender == null && receiver == null;
        }

        if (getSender() == null) {
            return sender == null && getReceiver().equals(receiver);
        }

        if (getReceiver() == null) {
            return receiver == null && getSender().equals(sender);
        }

        return (getSender().equals(sender) || getReceiver().equals(sender))
                &&
                (getSender().equals(receiver) || getReceiver().equals(receiver));
    }

    public String getSimpleForm() {
        return String.format("%s: %s", sender.getName(), message);
    }

    @Override
    public String toString() {
        if (isNew) {
            return String.format("(new) %s: %s", sender.getName(), message);
        } else {
            return String.format("%s: %s", sender.getName(), message);
        }
    }
}
