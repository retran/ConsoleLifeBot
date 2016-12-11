package me.retran.consolelifebot;

import com.google.inject.Inject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

public class CommandsHandler extends TelegramLongPollingBot {
    public static final String LOGTAG = "COMMANDSHANDLER";

    private final Configuration configuration;
    private final SentMessageCallback sentMessageCallback;

    @Inject
    CommandsHandler(Configuration configuration, SentMessageCallback sentMessageCallback) {
        this.configuration = configuration;
        this.sentMessageCallback = sentMessageCallback;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            BotLogger.info(update.getMessage().getFrom().getUserName(), text);
            if (text.equalsIgnoreCase("/kokoko")) {
                SendMessage sendMessage = new SendMessage()
                        .setText("кококо")
                        .setChatId(update.getMessage().getChatId())
                        .setReplyToMessageId(update.getMessage().getMessageId());
                try {
                    sendMessageAsync(sendMessage, this.sentMessageCallback);
                } catch (TelegramApiException e) {
                    BotLogger.severe(LOGTAG, e);
                }
            }
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
