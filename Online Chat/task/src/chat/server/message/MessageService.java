package chat.server.message;

import chat.server.dtos.StatisticDto;
import chat.server.user.User;
import chat.server.utils.PropertyHolder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MessageService {

    private static final File database = new File(PropertyHolder.getProperty("message_database"));

    private static MessageService singleton;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private List<Message> messages = new ArrayList<>();

    private MessageService() {
        try {
            initDatabase();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static synchronized MessageService getInstance() {
        if (singleton == null) {
            singleton = new MessageService();
        }
        return singleton;
    }

    public void addMessage(Message message) {
        try {
            writeLock.lock();
            messages.add(message);
            writeToDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public List<Message> getHistory(User you, User user, int requestedSize) {
        try {
            int size = Math.min(requestedSize, 25);
            readLock.lock();
            List<Message> history = messages.stream()
                    .filter(m -> m.isMessageOfChat(you, user))
                    .collect(Collectors.toList());
            if (history.size() > requestedSize) {
                return history.stream()
                        .skip(history.size() - requestedSize)
                        .limit(25)
                        .collect(Collectors.toList());
            }
            return history;
        } finally {
            readLock.unlock();
        }
    }

    public StatisticDto getStatistic(User you, User user) {
        try {
            readLock.lock();
            StatisticDto stats = new StatisticDto();
            for (Message message : messages) {
                if (message.isMessageOfChat(you, user)) {
                    stats.increaseCount();
                    if (message.getSender().equals(you)) {
                        stats.increaseFromYou();
                    } else {
                        stats.increaseFromUser();
                    }
                }
            }
            return stats;
        } finally {
            readLock.unlock();
        }
    }

    public String getUserWithUnreadMessages(User receiver) {
        if (receiver == null) {
            return "";
        }
        try {
            readLock.lock();
            return messages.stream()
                    .filter(Message::isNew)
                    .filter(m -> receiver.equals(m.getReceiver()))
                    .map(Message::getSender)
                    .map(User::getName)
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(" "));
        } finally {
            readLock.unlock();
        }
    }

    public List<Message> getLastMessages(User sender, User receiver) {
        try {
            readLock.lock();
            Map<Boolean, List<Message>> chat = messages.stream()
                    .filter(message -> message.isMessageOfChat(sender, receiver))
                    .collect(Collectors.partitioningBy(Message::isNew));

            // at most 10 messages
            List<Message> oldMessagesToSend = chat.get(false).size() > 10
                    ? chat.get(false).subList(chat.get(false).size() - 10, chat.get(false).size())
                    : chat.get(false);

            if (chat.get(true).size() > 15) {
                if (chat.get(true).size() > 25) {
                    return chat.get(true).subList(chat.get(true).size() - 25, chat.get(true).size());
                } else {
                    int numberOfOldMessagesToSend = 25 - chat.get(true).size();
                    List<Message> result = new ArrayList<>();
                    if (numberOfOldMessagesToSend > oldMessagesToSend.size()) {
                        result.addAll(oldMessagesToSend);
                    } else {
                        result.addAll(oldMessagesToSend.subList(oldMessagesToSend.size() - numberOfOldMessagesToSend,
                                oldMessagesToSend.size()));
                    }
                    result.addAll(chat.get(true));
                    return result;
                }
            } else {
                List<Message> result = new ArrayList<>();
                result.addAll(oldMessagesToSend);
                result.addAll(chat.get(true));
                return result;
            }
        } finally {
            readLock.unlock();
        }
    }

    private void initDatabase() throws IOException, ClassNotFoundException {
        if (database.exists()) {
            try (FileInputStream fis = new FileInputStream(database);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                writeLock.lock();
                messages.clear();
                while (true) {
                    try {
                        Message message = (Message) ois.readObject();
                        messages.add(message);
                    } catch (EOFException e) {
                        // End of file
                        break;
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    private synchronized void writeToDatabase() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(database.toString());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            for (Message message : messages) {
                oos.writeObject(message);
            }
        }
    }

}
