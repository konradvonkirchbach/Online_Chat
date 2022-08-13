package chat.client.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyHolder {

    private static final String PATH = "/Users/konradvonkirchbach/IdeaProjects/Online Chat/Online Chat/task/src/chat/client/resources/client_properties.properties";
    public static Properties properties;

    static {
        try (FileReader reader = new FileReader(PATH)) {
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
