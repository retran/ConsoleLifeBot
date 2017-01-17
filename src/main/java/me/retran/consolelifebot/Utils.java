package me.retran.consolelifebot;

import java.io.InputStream;
import java.util.Scanner;

import org.telegram.telegrambots.api.objects.User;

public class Utils {
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
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(filename);
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

    public static int distance(String a, String b) {
        int m = a.length(), n = b.length();
        int[] d1;
        int[] d2 = new int[n + 1];

        for (int i = 0; i <= n; i++)
            d2[i] = i;

        for (int i = 1; i <= m; i++) {
            d1 = d2;
            d2 = new int[n + 1];
            for (int j = 0; j <= n; j++) {
                if (j == 0)
                    d2[j] = i;
                else {
                    int cost = (a.charAt(i - 1) != b.charAt(j - 1)) ? 1 : 0;
                    if (d2[j - 1] < d1[j] && d2[j - 1] < d1[j - 1] + cost)
                        d2[j] = d2[j - 1] + 1;
                    else if (d1[j] < d1[j - 1] + cost)
                        d2[j] = d1[j] + 1;
                    else
                        d2[j] = d1[j - 1] + cost;
                }
            }
        }
        return d2[n];
    }
}
