package me.retran.consolelifebot.common;

import com.google.inject.Inject;
import me.retran.consolelifebot.handlers.Handler;
import me.retran.consolelifebot.handlers.HandlersRepository;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class MessagesHandler extends TelegramLongPollingBot {
    public static final String LOGTAG = "COMMANDSHANDLER";

    private final Configuration configuration;
    private final SentMessageCallback sentMessageCallback;
    private final HandlersRepository handlers;

    @Inject
    MessagesHandler(Configuration configuration, HandlersRepository handlers, SentMessageCallback sentMessageCallback) {
        this.configuration = configuration;
        this.sentMessageCallback = sentMessageCallback;
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
