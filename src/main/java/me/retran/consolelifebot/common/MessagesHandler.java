package me.retran.consolelifebot.common;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import me.retran.consolelifebot.Configuration;
import me.retran.consolelifebot.handlers.HandlersRepository;

@Singleton
public class MessagesHandler extends TelegramLongPollingBot {
    private final Configuration configuration;
    private final HandlersRepository handlers;

    @Inject
    MessagesHandler(Configuration configuration, HandlersRepository handlers) {
        this.configuration = configuration;
        this.handlers = handlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            handlers.getHandler(message).handle(this, message);
        }
    }

    @Override
    public String getBotUsername() {
        return this.configuration.telegramUserName();
    }

    @Override
    public String getBotToken() {
        return this.configuration.telegramToken();
    }
}
