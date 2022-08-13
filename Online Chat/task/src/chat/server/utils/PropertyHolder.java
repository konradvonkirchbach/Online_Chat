package chat.server.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyHolder {

    private static final String PATH = "/Users/konradvonkirchbach/IdeaProjects/Online Chat/Online Chat/task/src/chat/server/resources/server_properties.properties";
    public static Properties properties;

    static {
        try (FileReader reader = new FileReader(PATH)) {
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
