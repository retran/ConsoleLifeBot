package me.retran.consolelifebot.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Configuration {
    private static final String filename = "application.properties";
    private static final String prefix = "consolelifebot.";

    private Properties properties;

    @Inject
    public Configuration() {
        this.properties = new Properties();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
            try {
                this.properties.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't load application properties.", e);
        }
    }

    private String getProperty(String property) {
        return this.properties.getProperty(prefix + property);
    }

    public String telegramToken() {
        return getProperty("telegramToken");
    }

    public String telegramUserName() {
        return getProperty("telegramUserName");
    }

    public String youtubeApiKey() {
        return getProperty("youtubeApiKey");
    }

    public String giantbombApiKey() {
        return getProperty("giantbombApiKey");
    }

    public String[] giantbombPlatforms() {
        return getProperty("giantbombPlatforms").split(",");
    }

    public String library() {
        return getProperty("library");
    }

    public String channels() {
        return getProperty("channels");
    }

    public String topFilename() {
        return getProperty("topfilename");
    }

    public String chatName() {
        return "@consolelife";
    }
}
