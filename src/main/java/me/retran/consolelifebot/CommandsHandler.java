package me.retran.consolelifebot;

import com.google.inject.Inject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class CommandsHandler extends TelegramLongPollingBot {
    public static final String LOGTAG = "COMMANDSHANDLER";

    private final Configuration configuration;

    @Inject
    CommandsHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            if (text.toLowerCase().equalsIgnoreCase("/kokoko")) {
                SendMessage sendMessage = new SendMessage()
                        .setText("кококо")
                        .setChatId(update.getMessage().getChatId())
                        .setReplyToMessageId(update.getMessage().getMessageId());
                try {
                    sendMessage(sendMessage);
                } catch (TelegramApiException e) {
                    // TODO log

                    e.printStackTrace();
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
