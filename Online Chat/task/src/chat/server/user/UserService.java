package chat.server.user;

import chat.server.utils.HashingUtils;
import chat.server.utils.PropertyHolder;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserService {

    private final static File database = new File(PropertyHolder.getProperty("user_database"));

    private static UserService singelton;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Set<User> users = new HashSet<>();

    private UserService() {
        try {
            initDatabase();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public synchronized static UserService getInstance() {
        if (singelton == null) {
            singelton = new UserService();
        }
        return singelton;
    }

    public boolean usernameExists(String username) {
        if (username == null) {
            return false;
        }

        try {
            readLock.lock();
            return users.stream()
                    .map(User::getName)
                    .anyMatch(username::equals);
        } finally {
            readLock.unlock();
        }
    }

    public boolean authenticateUser(User user) {
        if (user == null) {
            return false;
        }

        try {
            readLock.lock();
            return users.stream()
                    .anyMatch(user::authenticate);
        } finally {
            readLock.unlock();
        }
    }

    public User findByName(String name) {
        User user = null;

        if (name == null) {
            return null;
        }

        try {
            readLock.lock();
            user = users.stream()
                    .filter(u -> name.equals(u.getName()))
                    .findAny()
                    .orElse(null);
        } finally {
            readLock.unlock();
        }

        return user;
    }

    public void addUser(User user) {
        try {
            writeLock.lock();
            users.add(user);
            writeToDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public void flush() {
        try {
            writeToDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDatabase() throws IOException, ClassNotFoundException {
        if (database.exists()) {
            try (FileInputStream fis = new FileInputStream(database);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                writeLock.lock();
                users.clear();
                while (true) {
                    try {
                        User user = (User) ois.readObject();
                        users.add(user);
                    } catch (EOFException e) {
                        // reading until everthing is in
                        break;
                    }
                }

                // add admin
                User admin = new User("admin",
                        HashingUtils.getFromUTF8ByteArrayAsString(PropertyHolder.getProperty("admin_password")),
                        Roles.ADMIN);

                users.add(admin);
            } finally {
                writeLock.unlock();
            }
        }
    }

    private synchronized void writeToDatabase() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(database.toString());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            for (User user : users) {
                oos.writeObject(user);
            }
        }
    }
}
