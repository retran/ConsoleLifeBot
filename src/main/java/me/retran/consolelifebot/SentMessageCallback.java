package me.retran.consolelifebot;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.updateshandlers.SentCallback;

public class SentMessageCallback implements SentCallback<Message> {
    public static final String LOGTAG = "SENTMESSAGECALLBACK";

    @Override
    public void onResult(BotApiMethod<Message> method, Message response) {

    }

    @Override
    public void onError(BotApiMethod<Message> method, TelegramApiRequestException apiException) {
        BotLogger.severe(LOGTAG, apiException);
    }

    @Override
    public void onException(BotApiMethod<Message> method, Exception exception) {
        BotLogger.severe(LOGTAG, exception);
    }
}
