package me.retran.consolelifebot.common;

import java.io.InputStream;
import java.util.Scanner;

import org.telegram.telegrambots.api.objects.User;

public class Helpers {
    public static String getDisplayName(User user) {
        String displayName = user.getUserName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = (user.getFirstName() + " " + user.getLastName()).trim();
        }
        return displayName;
    }

    public static String getPredefinedMessage(String filename) {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        InputStream in = Helpers.class.getClassLoader().getResourceAsStream(filename);
        Scanner scanner = new Scanner(in, "utf-8");
        while (scanner.hasNextLine()) {
            if (!firstLine) {
                sb.append("\n");
            }
            sb.append(scanner.nextLine());
        }
        scanner.close();
        return sb.toString();
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
