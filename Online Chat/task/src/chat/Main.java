package chat;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final String SENT_MATCHER = "\\ssent\\s";

    private static final Pattern PATTERN = Pattern.compile(SENT_MATCHER);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String line = null;

        while ((line = scanner.nextLine()) != null) {
            if (PATTERN.matcher(line).find()) {
                System.out.println(line.replaceAll(SENT_MATCHER, ": "));
            }
        }
    }
}
