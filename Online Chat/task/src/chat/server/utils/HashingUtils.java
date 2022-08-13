package chat.server.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class HashingUtils {

    private static final String HASH_ALGORITHM = "encryption_algorithm";

    private static MessageDigest MESSAGE_DIGEST = null;

    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance(PropertyHolder.properties.getProperty(HASH_ALGORITHM));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static byte[] hash(String hash) {
        return MESSAGE_DIGEST.digest(hash.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] getFromUTF8ByteArrayAsString(String utf8EncodedByteArray) {
        List<Byte> bytes = Arrays.stream(utf8EncodedByteArray.split(",\\s"))
                .map(Byte::valueOf)
                .collect(Collectors.toList());

        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }

        return byteArray;
    }

}
