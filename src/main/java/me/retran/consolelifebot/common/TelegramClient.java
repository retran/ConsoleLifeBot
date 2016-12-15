package me.retran.consolelifebot.common;

import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TelegramClient extends DefaultAbsSender {
    private final Configuration configuration;

    @Inject
    public TelegramClient(Configuration configuration) {
        super(ApiContext.getInstance(DefaultBotOptions.class));
        this.configuration = configuration;
    }

    @Override
    public String getBotToken() {
        return this.configuration.telegramToken();
    }
}
