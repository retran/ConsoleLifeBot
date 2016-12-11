package me.retran.consolelifebot;

import org.telegram.telegrambots.api.objects.User;

import java.io.InputStream;
import java.util.Scanner;

public class Helpers {
    public static String getDisplayName(User user) {
        String displayName = user.getUserName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = (user.getFirstName() + " " + user.getLastName()).trim();
        }
        return displayName;
    }

    public static String getPredefinedMessage(String filename) {
        String result = "";
        InputStream in = Helpers.class.getClassLoader()
                .getResourceAsStream(filename);
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            if (!result.isEmpty()) {
                result += "\n";
            }
            result += scanner.nextLine();
        }
        scanner.close();
        return result;
    }
}
