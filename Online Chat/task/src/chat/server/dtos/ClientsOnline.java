package chat.server.dtos;

import chat.server.user.User;
import chat.server.utils.ClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public final class ClientsOnline {

    private List<ClientHandler> clientsOnline = new ArrayList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final static ClientsOnline singleton = new ClientsOnline();

    private ClientsOnline() {

    }

    public void addClient(ClientHandler ch) {
        try {
            writeLock.lock();
            clientsOnline.add(ch);
        } finally {
            writeLock.unlock();
        }
    }

    public void removeUser(String username) {
        if (username == null) {
            return;
        }
        try {
            writeLock.lock();
            Optional<ClientHandler> optionalClient = clientsOnline.stream()
                    .filter(ch -> username.equals(ch.getUsername()))
                    .findFirst();
            optionalClient.ifPresent(clientsOnline::remove);
        } finally {
            writeLock.unlock();
        }
    }

    public void removeUser(ClientHandler client) {
        try {
            writeLock.lock();
            clientsOnline.remove(client);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isUserOnline(String username) {
        if (username == null) {
            return false;
        }
        try {
            readLock.lock();
            return clientsOnline.stream()
                    .map(ClientHandler::getUsername)
                    .anyMatch(username::equals);
        } finally {
            readLock.unlock();
        }
    }

    public ClientHandler findByUser(User user) {
        if (user == null) {
            return null;
        }
        try {
            readLock.lock();
            return clientsOnline.stream()
                    .filter(ch -> user.equals(ch.getUser()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(String.format("User %s no longer in chat", user.getName())));
        } finally {
            readLock.unlock();
        }
    }

    public Optional<ClientHandler> findByUser(String username) {
        if (username == null) {
            return null;
        }
        try {
            readLock.lock();
            return clientsOnline.stream()
                    .filter(ch -> username.equals(ch.getUsername()))
                    .findFirst();
        } finally {
            readLock.unlock();
        }
    }

    public boolean isUserOnline(ClientHandler client) {
        if (client == null) {
            return false;
        }
        try {
            readLock.lock();
            return clientsOnline.stream()
                    .anyMatch(client::equals);
        } finally {
            readLock.unlock();
        }
    }

    public String getOtherOnlineClientsName(ClientHandler clientHandler) {
        try {
            readLock.lock();
            return clientsOnline.stream()
                    .filter(ch -> !ch.equals(clientHandler))
                    .map(ClientHandler::getUsername)
                    .sorted()
                    .collect(Collectors.joining(" "));
        } finally {
            readLock.unlock();
        }
    }

    public boolean amITheOnlyOneOnline(ClientHandler clientHandler) {
        try {
            readLock.lock();
            return clientsOnline.size() == 1 && clientsOnline.get(0) == clientHandler;
        } finally {
            readLock.unlock();
        }
    }

    public static ClientsOnline getInstance() {
        return singleton;
    }

}
