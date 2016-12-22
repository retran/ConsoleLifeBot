package me.retran.consolelifebot.common;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
public class Configuration {
    private final String filename = "application.properties";
    private final String prefix = "consolelifebot.";

    private Properties properties;

    @Inject
    public Configuration() {
        this.properties = new Properties();
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(filename);
        try {
            this.properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String telegramToken() {
        return this.properties.getProperty(prefix + "telegramToken");
    }

    public String telegramUserName() {
        return this.properties.getProperty(prefix + "telegramUserName");
    }

    public String youtubeApiKey() {
        return this.properties.getProperty(prefix + "youtubeApiKey");
    }

    public String giantbombApiKey() {
        return this.properties.getProperty(prefix + "giantbombApiKey");
    }

    public String library() {
        return this.properties.getProperty(prefix + "library");
    }

    public String channels() {
        return this.properties.getProperty(prefix + "channels");
    }
}
