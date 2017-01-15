package me.retran.consolelifebot.messaging;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import me.retran.consolelifebot.common.Configuration;

@Singleton
public class TelegramService extends DefaultAbsSender {
    static {
        ApiContextInitializer.init();
    }

    private final Configuration configuration;

    @Inject
    public TelegramService(Configuration configuration) {
        super(ApiContext.getInstance(DefaultBotOptions.class));
        this.configuration = configuration;
    }

    @Override
    public String getBotToken() {
        return this.configuration.telegramToken();
    }
}
